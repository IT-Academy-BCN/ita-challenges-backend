package com.itachallenge.score.util;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.PullResponseItem;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.github.dockerjava.core.DockerClientBuilder.getInstance;

@Configuration
class DockerOptimizedExecutorHelper {

    @Bean
    public PullImageResultCallback mockPullImageResultCallback() {

        return new PullImageResultCallback() {

            @Override
            public void onNext(PullResponseItem item) {
                super.onNext(item);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
            }

            @Override
            public void onComplete() {
                super.onComplete();
            }

        };

    }

    @Bean
    public DockerClient basicDockerClient() {
        return getInstance().build();
    }

}
