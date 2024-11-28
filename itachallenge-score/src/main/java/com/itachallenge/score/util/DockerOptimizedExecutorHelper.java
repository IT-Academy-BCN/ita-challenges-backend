package com.itachallenge.score.util;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.PullResponseItem;
import com.github.dockerjava.api.model.ResponseItem;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.okhttp.OkDockerHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import static com.github.dockerjava.core.DockerClientBuilder.getInstance;
import static com.github.dockerjava.core.DefaultDockerClientConfig.createDefaultConfigBuilder;


@Configuration
class DockerOptimizedExecutorHelper {

    @Bean
    public PullImageResultCallback debuggingPullImageResultCallback() {

        return new PullImageResultCallback() {

            @Override
            public void onNext(PullResponseItem item) {

                if (item != null) {

                    String status = item.getStatus();
                    ResponseItem.ProgressDetail progressDetail = item.getProgressDetail();
                    String id = item.getId();

                    if (id != null && !id.isEmpty()) {

                        System.out.printf("ID: %s - Status: %s - Progress: %s/%s%n",
                                id,
                                status,
                                progressDetail != null ? progressDetail.getCurrent() : "N/A",
                                progressDetail != null ? progressDetail.getTotal() : "N/A");

                    } else
                        System.out.printf("Status: %s%n", status);

                }

                super.onNext(item);

            }

        };

    }

    @Bean
    public DockerClient basicDockerClient() {

        DefaultDockerClientConfig config = createDefaultConfigBuilder()
                .build();

        // Since future versions won't support Jersey we should specify an HTTP client
        OkDockerHttpClient httpClient = new OkDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .build();

        // Build the DockerClient with the specified HTTP client
        return getInstance(config)
                .withDockerHttpClient(httpClient)
                .build();

    }

}
