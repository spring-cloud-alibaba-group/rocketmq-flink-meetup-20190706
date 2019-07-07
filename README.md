spring cloud alibaba repo: https://github.com/spring-cloud-incubator/spring-cloud-alibaba

example topic: `binder-topic`

curl test:

```bash
curl http://localhost:10001/remoteEvent\?msg\=123\&dest\=node-2

curl http://localhost:10001/genericEvent\?msg\=11

curl http://localhost:8888/sendString\?msg\=helloworld

curl http://localhost:8888/sendUser\?id\=1\&name\=alibaba

```