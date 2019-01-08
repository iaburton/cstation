package cstation

import (
	"crypto/tls"
	"crypto/x509"
	"net"
	"os"
	"path/filepath"
	"testing"
	"time"
)

var tlsServAddr = "testserv:443"

func TestCertCap(t *testing.T) {
	readCertTest(t, createCertTest(t))
}

func createCertTest(t *testing.T) string {
	cert, err := getCert(tlsServAddr, true)
	if err != nil {
		t.Fatalf("Unable to get cert: %v", err)
	}

	certFile := filepath.Join(os.TempDir(), "cstation_cert_test.pem")
	if err = saveCert(certFile, cert); err != nil {
		t.Fatalf("Unable to save cert: %v", err)
	}

	return certFile
}

func readCertTest(t *testing.T, file string) {
	defer os.Remove(file)

	cert, err := readCert(file)
	if err != nil {
		t.Fatalf("Unable to read cert: %v", err)
	}

	cp := x509.NewCertPool()
	cp.AddCert(cert)

	conn, err := tls.DialWithDialer(&net.Dialer{Timeout: time.Second * 5}, "tcp", tlsServAddr, &tls.Config{RootCAs: cp})
	if err != nil {
		t.Fatalf("Unable to connect with cert: %v", err)
	}

	cs := conn.ConnectionState()
	conn.Close()
	t.Logf("CS: %+v\nSuccess!", cs)
}
