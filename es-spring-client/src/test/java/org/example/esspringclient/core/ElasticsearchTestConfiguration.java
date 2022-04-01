package org.example.esspringclient.core;

import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;

public class ElasticsearchTestConfiguration {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public ElasticsearchContainer elasticsearchContainer() {
        return new ElasticsearchContainer(
                DockerImageName.parse("elasticsearch:7.17.1")
                        .asCompatibleSubstituteFor("docker.elastic.co/elasticsearch/elasticsearch"))
                .withExposedPorts(9200, 9300)
                .withPassword("elastic")
                .waitingFor(Wait.forListeningPort());
    }

}
