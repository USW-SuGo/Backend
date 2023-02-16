package com.usw.sugo.global.util.imagelinkfiltering;

import com.usw.sugo.domain.note.note.dto.NoteResponseDto.LoadNoteListForm;
import com.usw.sugo.domain.productpost.productpost.controller.dto.PostResponseDto.ClosePosting;
import com.usw.sugo.domain.productpost.productpost.controller.dto.PostResponseDto.DetailPostResponse;
import com.usw.sugo.domain.productpost.productpost.controller.dto.PostResponseDto.LikePosting;
import com.usw.sugo.domain.productpost.productpost.controller.dto.PostResponseDto.MainPageResponse;
import com.usw.sugo.domain.productpost.productpost.controller.dto.PostResponseDto.MyPosting;
import com.usw.sugo.domain.productpost.productpost.controller.dto.PostResponseDto.SearchResultResponse;
import org.springframework.stereotype.Component;

@Component
public class ImageLinkCharacterFilter {

    public MainPageResponse filterImageLink(MainPageResponse mainPageResponse) {
        if (mainPageResponse.getImageLink() == null) {
            mainPageResponse.setImageLink("");
        } else {
            String imageLink = mainPageResponse.getImageLink()
                .replace("[", "")
                .replace("]", "");
            mainPageResponse.setImageLink(imageLink);
        }
        return mainPageResponse;
    }

    public MyPosting filterImageLink(MyPosting myPosting) {
        if (myPosting.getImageLink() == null) {
            myPosting.setImageLink("");
        } else {
            String imageLink = myPosting.getImageLink().split(",")[0]
                .replace("[", "")
                .replace("]", "");
            myPosting.setImageLink(imageLink);
        }
        return myPosting;
    }

    public SearchResultResponse filterImageLink(SearchResultResponse searchResultResponse) {
        if (searchResultResponse.getImageLink() == null) {
            searchResultResponse.setImageLink("");
        } else {
            String imageLink = searchResultResponse.getImageLink()
                .replace("[", "")
                .replace("]", "");
            searchResultResponse.setImageLink(imageLink);
        }
        return searchResultResponse;
    }

    public DetailPostResponse filterImageLink(DetailPostResponse detailPostResponse) {
        if (detailPostResponse.getImageLink() == null) {
            detailPostResponse.setImageLink("");
        } else {
            String imageLink = detailPostResponse.getImageLink()
                .replace("[", "")
                .replace("]", "");
            detailPostResponse.setImageLink(imageLink);
        }
        return detailPostResponse;
    }

    public ClosePosting filterImageLink(ClosePosting closePosting) {
        if (closePosting.getImageLink() == null) {
            closePosting.setImageLink("");
        } else {
            String imageLink = closePosting.getImageLink().split(",")[0]
                .replace("[", "")
                .replace("]", "");
            closePosting.setImageLink(imageLink);
        }
        return closePosting;
    }

    public LikePosting filterImageLink(LikePosting likePosting) {
        if (likePosting.getImageLink() == null) {
            likePosting.setImageLink("");
        } else {
            String imageLink = likePosting.getImageLink().split(",")[0]
                .replace("[", "")
                .replace("]", "");
            likePosting.setImageLink(imageLink);
        }
        return likePosting;
    }
}
