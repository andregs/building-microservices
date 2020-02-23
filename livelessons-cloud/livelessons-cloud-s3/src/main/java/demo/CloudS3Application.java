package demo;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.aws.context.config.annotation.EnableContextResourceLoader;
import org.springframework.cloud.aws.core.io.s3.SimpleStorageProtocolResolver;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.util.FileCopyUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

@SpringBootApplication
@EnableContextResourceLoader
public class CloudS3Application {

    @Value("${livelessons.s3.bucket}")
    private String bucket;

	@Value("${cloud.aws.credentials.access-key}")
	private String accessKey;

	@Value("${cloud.aws.credentials.secret-key}")
	private String secretKey;

	@Value("${cloud.aws.region.static}")
	private String region;

    private final DefaultResourceLoader resourceLoader;

	public CloudS3Application(AmazonS3 amazonS3, DefaultResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

    @PostConstruct
    public void resourceAccess() throws IOException {
        workaround();

        String location = "s3://" + this.bucket + "/file.txt";
        WritableResource writeableResource = (WritableResource) this.resourceLoader
                .getResource(location);
        FileCopyUtils.copy("Hello World!",
                new OutputStreamWriter(writeableResource.getOutputStream()));

        Resource resource = this.resourceLoader.getResource(location);
        System.out.println(FileCopyUtils
                .copyToString(new InputStreamReader(resource.getInputStream())));
    }

    public void workaround() {
	    // FIXME this is a workaround due to a regression in spring-cloud-aws
        // https://github.com/spring-cloud/spring-cloud-aws/issues/348
	    // https://stackoverflow.com/a/59078881/259237
        // remove this method when the issue is fixed
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withRegion(region)
                .build();
        SimpleStorageProtocolResolver simpleStorageProtocolResolver = new SimpleStorageProtocolResolver(amazonS3);
        simpleStorageProtocolResolver.afterPropertiesSet();
        resourceLoader.addProtocolResolver(simpleStorageProtocolResolver);
    }

    public static void main(String[] args) {
        SpringApplication.run(CloudS3Application.class, args);
    }

}
