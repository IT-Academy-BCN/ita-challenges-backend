package com.itachallenge.userinteraction.document.bookmark;

import com.itachallenge.userinteraction.document.InteractionDocument;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Document(collection="bookmarks")
public class BookmarkDocument extends InteractionDocument {}
