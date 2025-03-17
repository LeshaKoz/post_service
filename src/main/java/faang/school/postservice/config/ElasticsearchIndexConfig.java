package faang.school.postservice.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.io.IOException;


@Configuration
public class ElasticsearchIndexConfig {

    @Value("${spring.elasticsearch.index.shards}")
    private int shards;

    @Value("${spring.elasticsearch.index.replicas}")
    private int replicas;

    @Bean
    @DependsOn("elasticsearchClient")
    public boolean initializeElasticsearchIndexes(ElasticsearchClient elasticsearchClient) throws IOException {
        createHashtagsIndex(elasticsearchClient);
        return true;
    }

    private void createHashtagsIndex(ElasticsearchClient elasticsearchClient) throws IOException {
        String indexName = "hashtags_index";

        boolean indexExists = elasticsearchClient.indices().exists(
                new ExistsRequest.Builder().index(indexName).build()
        ).value();

        if (!indexExists) {
            elasticsearchClient.indices().create(c -> c
                    .index(indexName)
                    .settings(s -> s
                            .numberOfShards(String.valueOf(shards))
                            .numberOfReplicas(String.valueOf(replicas))
                    )
                    .mappings(m -> m
                            .properties("post_id", p -> p.keyword(k -> k))
                            .properties("hashtags", p -> p.keyword(k -> k))
                            .properties("content", p -> p.text(t -> t))
                            .properties("created_at", p -> p.date(d -> d))
                    )
            );
        }
    }
}
