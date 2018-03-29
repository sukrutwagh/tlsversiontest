# Overview
Sample client to test the behavior of http client for invoking https endpoints.
Simulation tested with Apache httpd 2.4.23
HTTP client library used are obselete/depricated/old as my use case involved checking TLS compatibility of an legacy product which
required me to use exactly the same libraries

This project might same some time in setting up a quick HTTPS server environment

# Set up HTTPD server
Install & start Apache httpd server
Configure httpd.conf to host multiple https endpoints with different TLS version support
Tune per your needs

<VirtualHost *:8443>
	LogLevel debug
    ServerName localhost
    SSLEngine on
	SSLProtocol -all +TLSv1.2
	SSLCipherSuite ALL:!aNULL:RC4+RSA:+HIGH:+MEDIUM:+LOW:+EXP:+eNULL
    SSLCertificateFile "conf/ssl/server.crt"
    SSLCertificateKeyFile "conf/ssl/server.key"
</VirtualHost>

<VirtualHost *:8444>
	LogLevel debug
    ServerName localhost
    SSLEngine on
	SSLProtocol -all +TLSv1.1 +TLSv1.2
	SSLCipherSuite ALL:!aNULL:RC4+RSA:+HIGH:+MEDIUM:+LOW:+EXP:+eNULL
    SSLCertificateFile "conf/ssl/server.crt"
    SSLCertificateKeyFile "conf/ssl/server.key"
</VirtualHost>

<VirtualHost *:8445>
	LogLevel debug
    ServerName localhost
    SSLEngine on
	SSLProtocol -all +TLSv1
	SSLCipherSuite ALL:!aNULL:RC4+RSA:+HIGH:+MEDIUM:+LOW:+EXP:+eNULL
    SSLCertificateFile "conf/ssl/server.crt"
    SSLCertificateKeyFile "conf/ssl/server.key"
</VirtualHost>

# Check HTTPD server's endpoint TLS version support
https://github.com/pornin/TestSSLServer

# Test your java clien't compatibility
Tune TLSChecker per your needs