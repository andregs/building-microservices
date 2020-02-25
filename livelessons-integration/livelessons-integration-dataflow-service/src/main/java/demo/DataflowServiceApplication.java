package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.dataflow.server.EnableDataFlowServer;

/* Spring Cloud Data Flow requires a server. You have different options to spin up a new server: https://dataflow.spring.io/docs/installation/
 *  - This project is an example of a server created programmatically. You can control it with the Shell available at https://dataflow.spring.io/docs/installation/local/docker/#shell
 *  - However, I had issues because it seems we are missing a Skipper server. As a plan B, I created our servers via Docker.
 * First, we have to download the docker compose YMLs we are going to use:
 *  - wget https://raw.githubusercontent.com/spring-cloud/spring-cloud-dataflow/master/spring-cloud-dataflow-server/docker-compose.yml -OutFile docker-compose.yml
 *  - wget https://raw.githubusercontent.com/spring-cloud/spring-cloud-dataflow/master/spring-cloud-dataflow-server/docker-compose-rabbitmq.yml -OutFile docker-compose-rabbitmq.yml
 * Then, we have to add in docker-compose.yml a volume mapping to our local maven repository (where we have previously installed our livelessons-integration-consumer app):
 * volumes:
      - ~/.m2:/root/.m2
 * I added that mapping to both dataflow-server and skipper-server, as per instructions in https://dataflow.spring.io/docs/installation/local/docker-customize/#accessing-the-host-file-system
 * Then we can bring the services up (I'm using PowerShell):
 *  - $env:DATAFLOW_VERSION="2.4.0.RELEASE"
 *  - $env:SKIPPER_VERSION="2.3.0.RELEASE"
 *  - docker-compose.exe -f .\docker-compose.yml -f .\docker-compose-rabbitmq.yml up
 *
 * It takes a while, and you can check the status with:
 *  - docker-compose.exe -f .\docker-compose.yml -f .\docker-compose-rabbitmq.yml ps
 *
 * All services except kafka and zookeeper must be "Up". In my case, I had to manually restart dataflow-server and skipper-server:
 *  - docker-compose.exe -f .\docker-compose.yml -f .\docker-compose-rabbitmq.yml restart dataflow-server
 *  - docker-compose.exe -f .\docker-compose.yml -f .\docker-compose-rabbitmq.yml restart skipper-server
 *
 * After that, I got all servers "Up" in docker-compose ps command.
 *
 * Once Docker is finished, we can use the Shell to control our data flow server:
 *  - docker exec -it dataflow-server java -jar shell.jar
 *
 * Josh uses a bit.ly URL in the video, but the current list of available starters can be found at: https://docs.spring.io/spring-cloud-dataflow/docs/current/reference/htmlsingle/#_spring_cloud_stream_app_starters
 *  - app import --uri https://dataflow.spring.io/rabbitmq-maven-latest
 *
 * The commands to create our demo stream:
 *  - app register --name demo-consumer --type sink --uri maven://livelessons:livelessons-integration-consumer:1.0.0-SNAPSHOT
 *  - stream create --name http-to-demo-consumer --definition "http --port=8030 | demo-consumer" --deploy
 *
 * There is a fancy dashboard where you can check your stream (and copy log file paths):
 *  - http://localhost:9393/dashboard/#/runtime/apps
 *
 * You can log into the containers to play around. In the following example, I'll log into skipper server to
 * post data and then inspect the results in the logs (your path will be different - check in the dashboard):
 *  - docker exec -it skipper bash
 *  - wget --header="content-type:application/json" --post-data="Hello" http://localhost:8030/
 *  - cat /tmp/1582523922682/http-to-demo-consumer.demo-consumer-v1/stdout_0.log
 * You can see on the above commands we posted a "Hello" string to "http" app and it ended up in our consumer log,
 * because http output channel is piped into demo-consumer input channel.
 *
 * TODO I was not able to post data from my host machine to the http app that lives in skipper docker container.
 * If you find a way to do that, please let me know.
 * TODO Another nice thing to try would be the setup on Cloud Foundry instead of local docker.
 */
@EnableDataFlowServer
@SpringBootApplication
public class DataflowServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataflowServiceApplication.class, args);
	}
}
