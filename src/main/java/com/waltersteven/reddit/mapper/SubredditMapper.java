package com.waltersteven.reddit.mapper;

import com.waltersteven.reddit.dto.SubredditDto;
import com.waltersteven.reddit.model.Post;
import com.waltersteven.reddit.model.Subreddit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubredditMapper {

    @Mapping(target = "numberOfPosts", expression = "java(mapPosts(subreddit.getPosts()))")
    SubredditDto mapSubredditToDto(Subreddit subreddit);

    default Integer mapPosts(List<Post> posts) {
        return posts.size();
    }

    @Mapping(target = "posts", ignore = true)
    Subreddit mapDtoToSubreddit(SubredditDto subredditDto);
}
