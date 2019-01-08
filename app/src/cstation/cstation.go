package cstation

import (
	"context"
	"log"
	"net"
	"os"
	"path/filepath"
	"time"

	"crypto/tls"
	"crypto/x509"
	"net/http"
	"net/url"

	"golang.org/x/net/http2"

	cstation "github.com/iaburton/go-containerstation"
)

//import "Java/java/lang/System"

var (
	client     *cstation.Client
	appBaseDir string
)

func InitLibrary(baseDir, baseURL string, downloadCert bool) error {
	if client != nil {
		return nil
	}

	appBaseDir = baseDir
	var c *cstation.Client
	var err error
	if downloadCert {
		c, err = clientWithCert(baseURL)
	} else {
		c, err = defaultClient(baseURL)
	}
	if err != nil {
		return err
	}
	client = c
	return nil
}

func Login(user, pass string) (err error) {
	var lr *cstation.LoginResponse
	lr, err = client.Login(context.Background(), user, pass)
	log.Printf("Go/Login: %+v", lr)
	return
}

func Logout() (err error) {
	var lo *cstation.LogoutResponse
	lo, err = client.Logout(context.Background())
	log.Printf("Go/Logout: %+v", lo)
	return
}

func defaultClient(baseURL string) (*cstation.Client, error) {
	c := cstation.NewClient(baseURL, nil)
	return &c, nil
}

func clientWithCert(baseURL string) (*cstation.Client, error) {
	ul, err := url.Parse(baseURL)
	if err != nil {
		return nil, err
	}

	certFile := filepath.Join(appBaseDir, ul.Host+".pem")
	cert, err := readCert(certFile)
	if err != nil {
		if os.IsNotExist(err) {
			addr := ul.Host
			if _, _, err = net.SplitHostPort(ul.Host); err != nil {
				//assume we're missing the port, add default https 443
				addr += ":443"
			}

			//this code path should only be taken when the cert is self signed
			cert, err = getCert(addr, true)
			if err != nil {
				return nil, err
			}

			//save cert so we don't have to do this again
			//if we get an error, we could continue but then we'd have to connect to
			//the server again next time, instead return the error
			if err = saveCert(certFile, cert); err != nil {
				return nil, err
			}
		} else {
			return nil, err
		}
	}

	cp := x509.NewCertPool()
	cp.AddCert(cert)

	transport := &http.Transport{
		Proxy: http.ProxyFromEnvironment,
		DialContext: (&net.Dialer{
			Timeout:   30 * time.Second,
			KeepAlive: 30 * time.Second,
			DualStack: true,
		}).DialContext,
		MaxIdleConns:          5,
		IdleConnTimeout:       120 * time.Second,
		TLSHandshakeTimeout:   10 * time.Second,
		ExpectContinueTimeout: 1 * time.Second,
		TLSClientConfig: &tls.Config{
			RootCAs:    cp,
			MinVersion: tls.VersionTLS12,
		},
	}
	if err = http2.ConfigureTransport(transport); err != nil {
		return nil, err
	}

	c := cstation.NewClient(baseURL, &http.Client{Transport: transport, Timeout: time.Second * 10})
	return &c, nil
}
