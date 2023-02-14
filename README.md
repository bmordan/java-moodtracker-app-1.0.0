# SpringBoot with OAuth

* You will need a couple of Auth0 accounts

username: test@test.com
password: testPassw0rd!

username: other@test.com
password: testPassw0rd!

You will need to add your Auth0 CLIENT_ID and CLIENT_SECRET as environmental variables:

```
MOD2_AUTH0_CLIENT_ID=**************************
MOD2_AUTH0_CLIENT_SECRET=**************************
MOD2_AUTH0_ISSUER=**************************
```

```
gradle bootRun
```
_you will need gradle installed on your command line to run that command to start your springboot server_

## Run Jenkins

```
docker-compose up -d
```

```
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

### Hello World Pipeline

```ruby
pipeline {
    agent any 
    stages {
        stage('Stage 1') {
            steps {
                echo 'Hello world!' 
            }
        }
    }
}
```

## Notes

* [Build a Jenkins with Docker in it](https://medium.com/the-devops-ship/custom-jenkins-dockerfile-jenkins-docker-image-with-pre-installed-plugins-default-admin-user-d0107b582577)
* I built `bmordan/jenkins-with-docker` using the `Dockerfile.Jenkins` in this repo
* https://www.thesunflowerlab.com/jenkins-aws-ec2-instance-ssh/ AWS PEM file

#### Plugins to add

Pipeline: Declarative
Docker Pipeline
Gradle
Docker API Plugin
Docker Commons Plugin
Docker Pipeline
Docker Plugin
docker-build-step

Add docker to cloud in manage jenkins and set the docker host uri on Mac that is:

```
unix://var/run/docker.sock
```

## AWS

Get docker on your EC2 instance

```
sudo yum update -y 
sudo amazon-linux-extras install docker  
sudo curl -L https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m) -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
sudo usermod -a -G docker ec2-user
sudo service docker start
sudo systemctl enable docker.service
```
Restart that puppy.

### HTTPS

Install Apache web server
```sh
sudo yum install -y httpd
sudo systemctl start httpd
sudo systemctl enable httpd
```
To check all is set up visit `http://my.ip.addr.here` you should see the Apache test page. Notice the `http://` nothing will show if you request via `https://` that is what we are working on now.

![Apache web server test page](https://docs.aws.amazon.com/images/AWSEC2/latest/UserGuide/images/apache_test_page_al2_2.4.png)

You can now put static assets into `/var/www/html/` folder and they will be served at your host address `http://my.ip.addr.here`. Add your ec2 user to the apache group.

```sh
sudo usermod -a -G apache ec2-user
```

Log out and log back in again to pick up the group `exit` -> reconnect with your ssh command.
Change the group ownership of `/var/www` and its contents and future contents to the apache group.

```
sudo chown -R ec2-user:apache /var/www
sudo chmod 2775 /var/www && find /var/www -type d -exec sudo chmod 2775 {} \;
find /var/www -type f -exec sudo chmod 0664 {} \;
```

### Certbot Setup

```
sudo wget -r --no-parent -A 'epel-release-*.rpm' https://dl.fedoraproject.org/pub/epel/7/x86_64/Packages/e/
sudo rpm -Uvh dl.fedoraproject.org/pub/epel/7/x86_64/Packages/e/epel-release-*.rpm
sudo yum-config-manager --enable epel*
sudo yum repolist all
```
Find `/etc/httpd/conf/httpd.conf` find "Listen 80" in the file and add the following
You need to actually have a domain name you can point at your AWS ip address. This will not work without DNS.
```xml
<VirtualHost *:80>
    DocumentRoot "/var/www/html"
    ServerName "example.com"
    ServerAlias "www.example.com"
</VirtualHost>
```

### Install Certbot

```
sudo amazon-linux-extras install epel -y
sudo yum install -y certbot python2-certbot-apache
sudo certbot
```
Follow the prompts - get your certs installed.
open `/etc/crontab`
Add the cron job (twice daily cert checking)

```
39      1,13    *       *       *       root    certbot renew --no-self-upgrade
```
Then restart the cron daemon `sudo systemctl restart crond`

### Proxy requests from port 80 to 8080

This is going to pass https trafic from port 80 (which has apache running on it) to port 8080 which has our moodtracker app running on it.

There are 3 files we need to edit

1. `/etc/httpd/conf/httpd.conf`
1. `/etc/httpd/conf/httpd-le-ssl.conf`
1. `/etc/httpd/conf.d/ssl.conf`

In `/etc/httpd/conf/httpd.conf` update the VirtualHost definition to this

```
Listen 80
<VirtualHost *:80>
    ProxyPreserveHost On
    ProxyRequests Off
    DocumentRoot "/var/www/html"
    ServerName "example.com"
    ServerAlias "www.example.com"
    ProxyPass / http://127.0.0.1:8080/
    ProxyPassReverse / https://127.0.0.1:8080/
    RewriteEngine on
    RewriteCond %{SERVER_NAME} =www.example.com [OR]
    RewriteCond %{SERVER_NAME} =example.com
    RewriteRule ^ https://%{SERVER_NAME}%{REQUEST_URI} [END,NE,R=permanent]
</VirtualHost>
```
In `/etc/httpd/conf/httpd-le-ssl.conf` add similar config - notice this is for port `:443` which is the port dealing with encrypted https requests.

```
<VirtualHost *:443>
    DocumentRoot "/var/www/html"
    ServerName "example.com"
    ServerAlias "www.example.com"
    SSLProxyEngine on
    ProxyPreserveHost On
    ProxyPass / http://127.0.0.1:8080/
    ProxyPassReverse / https://127.0.0.1:8080/
    Include /etc/letsencrypt/options-ssl-apache.conf
    SSLCertificateFile /etc/letsencrypt/live/bernardmordan.dev/fullchain.pem
    SSLCertificateKeyFile /etc/letsencrypt/live/bernardmordan.dev/privkey.pem
</VirtualHost>
```

In `/etc/httpd/conf.d/ssl.conf` just add `SSLProxyEngine on` the line below `SSLEngine on`.

Now you can update the redirect URLs on Auth0 and your moodtracking app should be provisioned.