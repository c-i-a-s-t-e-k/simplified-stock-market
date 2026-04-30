# simplified-stock-market

The project implements task described in /doc/task_for)intership_2026.md, the main goal is implement the 
concurency resilient system witch bank and stocks wich can be bought by wallets, there is simple fluidy.
If you need more technical info about the project go to ./TECHNICAL.md

## Setup and start

to run project there will be needed to have docker and avaiable internet connetion do dowaland all needed 
java dependencies and postgressqlo database docker image.

build and run apps you go with 
```bash
chmod +x ./build_and_run.sh
./build_and_run.sh 8081 8082 8083 #number of ports, 1 port = 1 app instance
```

if already builded you can get a fresh envoirement wich commands this commands will work on conteiners created
with build so if you wanna set-up diffren number of istances then you nned to remove this envoriment and then buld another.:
```bash
chmod +x ./start.sh
chmod +x ./stop.sh

./stop.sh
./start.sh
```

to remove cointeiners and clean-up you use remove.sh
```bash
chmod +x ./remove.sh
./remove.sh
```

## Rest Server
app have avaiable endpoints api as specyfied in /doc/task_for)intership_2026.md, adiitionali its avaiable to get OpenAPI documetion at endpoints
```txt
/swagger-ui/index.html # human redable api endpoints doc
v3/api-docs # json containing all avaiable api
v3/api-docs.yaml # as above but its .yaml file
```

More about what errors return each api in ./TECHNICAL.md

