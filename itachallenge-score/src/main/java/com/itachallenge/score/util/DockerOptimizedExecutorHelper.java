package com.itachallenge.score.util;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.PullResponseItem;
import com.github.dockerjava.api.model.ResponseItem;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.zerodep.ZerodepDockerHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import static com.github.dockerjava.core.DefaultDockerClientConfig.createDefaultConfigBuilder;
import static com.github.dockerjava.core.DockerClientBuilder.getInstance;


@Configuration
class DockerOptimizedExecutorHelper {

    @Bean
    public PullImageResultCallback basicPullImageResultCallback() {

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

        DockerClientConfig config = createDefaultConfigBuilder().build();
        // TODO Default Docker daemon is used using auto-detection
        // TODO Default Docker Hub Registry used

        // Http Client defined since the Jersey one will stop being used in the future
        ZerodepDockerHttpClient httpClient = new ZerodepDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .build();

        return getInstance(config)
                .withDockerHttpClient(httpClient)
                .build();

    }

}
