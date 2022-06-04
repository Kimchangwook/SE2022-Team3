package closet.backend.service;

import closet.backend.Util.AuthUtil;
import closet.backend.dao.PostDao;
import closet.backend.dto.PostDtoWithCommentCount;
import com.google.firebase.auth.FirebaseAuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MyPageService {

    @Autowired
    private final PostDao postDao;
    @Autowired
    private final AuthUtil authUtil;

    public List<PostDtoWithCommentCount> getPosts(String idToken) throws FirebaseAuthException {
        int id = authUtil.getUserid(idToken);
        List<PostDtoWithCommentCount> result = postDao.findById(id);
        return result;
    }

    public List<PostDtoWithCommentCount> getFavoritePost(String idToken) throws FirebaseAuthException {
        int id = authUtil.getUserid(idToken);
        List<PostDtoWithCommentCount> result = postDao.findFavoritePost(id);
        return result;
    }
}
