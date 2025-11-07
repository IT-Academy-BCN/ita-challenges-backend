package com.itachallenge.userinteraction.service.favorite;

import com.itachallenge.userinteraction.repository.favorite.FavoriteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

    @ExtendWith(MockitoExtension.class)
    class FavoriteServiceImplTest {

        @Mock
        private FavoriteRepository favoriteRepository;

        private FavoriteServiceImpl favoriteService;

        @BeforeEach
        void setUp() {
            favoriteService = new FavoriteServiceImpl(favoriteRepository);
        }

        @Test
        void constructor_ShouldInitializeRepository_test() {
            FavoriteRepository testRepository = mock(FavoriteRepository.class);

            FavoriteServiceImpl service = new FavoriteServiceImpl(testRepository);

            assert service != null;
        }


}
