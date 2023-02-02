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

Add docker to cloud in manage jenkins and set the docker host uri on Mac that is:

```
unix://var/run/docker.sock
```

## AWS

Get docker on your EC2 instance

```
sudo yum update -y
sudo yum install docker -y
sudo service docker start
sudo usermod -a -G docker ec2-user
```