package cstation

import (
	"crypto/tls"
	"crypto/x509"
	"encoding/pem"
	"fmt"
	"io/ioutil"
	"net"
	"os"
	"time"
)

func getCert(addr string, skipVerify bool) (*x509.Certificate, error) {
	var config *tls.Config
	if skipVerify { //we want config to be nil if not true
		config = &tls.Config{InsecureSkipVerify: true}
	}

	conn, err := tls.DialWithDialer(&net.Dialer{Timeout: time.Second * 5}, "tcp", addr, config)
	if err != nil {
		return nil, err
	}

	cs := conn.ConnectionState()
	return cs.PeerCertificates[0], conn.Close()
}

func saveCert(file string, cert *x509.Certificate) error {
	fh, err := os.OpenFile(file, os.O_WRONLY|os.O_CREATE, 0660)
	if err != nil {
		return err
	}
	defer fh.Close()

	return pem.Encode(fh, &pem.Block{
		Type:  "CERTIFICATE",
		Bytes: cert.Raw,
	})
}

func readCert(file string) (*x509.Certificate, error) {
	fi, err := os.Stat(file)
	if err != nil {
		return nil, err
	}

	if fi.Size() > (1024 * 1024 * 5) {
		return nil, fmt.Errorf("cstation: %s does not appear to be a certificate", file)
	}

	data, err := ioutil.ReadFile(file)
	if err != nil {
		return nil, err
	}

	b, _ := pem.Decode(data)
	if b == nil {
		return nil, fmt.Errorf("cstation: Unable to find PEM block in (%s)", file)
	}

	//Assume it's a cert and there is only one
	return x509.ParseCertificate(b.Bytes)
}
