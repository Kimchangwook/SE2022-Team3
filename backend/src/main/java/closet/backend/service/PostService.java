package closet.backend.service;

import closet.backend.dto.post.*;
import closet.backend.req.post.UpdatePostReq;
import closet.backend.util.AuthUtil;
import closet.backend.util.FileUtil;
import closet.backend.dao.post.FavoritePostDao;
import closet.backend.dao.post.PostDao;
import closet.backend.exception.PostException;
import closet.backend.req.post.ChangePostFavoriteReq;
import closet.backend.req.post.CreatePostReq;
import closet.backend.req.post.DeletePostReq;
import com.google.firebase.auth.FirebaseAuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {

    @Autowired
    private final PostDao postDao;
    @Autowired
    private final FavoritePostDao favoritePostDao;
    @Autowired
    private final AuthUtil authUtil;
    private final FileUtil fileUtil;

    public PostDto createPost(CreatePostReq createPostReq, Optional<MultipartFile> img) throws FirebaseAuthException, IOException {
        int id = authUtil.getUserId(createPostReq.getIdToken());
        String file_name;
        if(img.isPresent()){
            file_name = fileUtil.uploadFile(img.get());
        }else{
            file_name = "";
        }
        CreatePostDto createPostDto = new CreatePostDto(id, createPostReq.getTitle(), createPostReq.getGenre(), createPostReq.getPost_body(), file_name);
        PostDto postDto = postDao.save(createPostDto);
        return postDto;
    }

    public String deletePost(DeletePostReq deletePostReq) throws FirebaseAuthException{
        int id = authUtil.getUserId(deletePostReq.getIdToken());
        if(id == postDao.findWriterId(deletePostReq.getPost_id())){
            String result = postDao.delete(deletePostReq.getPost_id());
            return result;
        } else{
            throw new PostException("글 작성자가 아닙니다.",403);
        }
    }

    public PostDto updatePost(UpdatePostReq updatePostReq, Optional<MultipartFile> img) throws FirebaseAuthException, IOException {
        int id = authUtil.getUserId(updatePostReq.getIdToken());
        String file_name;
        if( id == postDao.findWriterId(updatePostReq.getPost_id())){
            if(img.isPresent()){
                file_name = fileUtil.uploadFile(img.get());
            }else{
                file_name = "";
            }
            UpdatePostDto updatePostDto = new UpdatePostDto(id, updatePostReq.getPost_id(), updatePostReq.getTitle(), updatePostReq.getGenre(), updatePostReq.getPost_body(), file_name);
            PostDto result = postDao.update(updatePostDto);
            return result;
        } else{
            throw new PostException("글 작성자가 아닙니다.",403);
        }
    }

    public PostDetailDto getPostDetail(int post_id, String idToken) throws FirebaseAuthException{
        int id = authUtil.getUserId(idToken);
        PostDetailDto result = postDao.findByPostId(post_id, id);
        return result;
    }

    public List<PostDtoWithCommentCount> getPostByGenre(String genre){
        List<PostDtoWithCommentCount> result = postDao.findByGenre(genre);
        return result;
    }

    public List<PostDtoWithCommentCount> getPosts(){
        List<PostDtoWithCommentCount> result = postDao.findAll();
        return result;
    }

    public PostDetailDto changeFavorite(ChangePostFavoriteReq changePostFavoriteReq) throws FirebaseAuthException{
        int id = authUtil.getUserId(changePostFavoriteReq.getIdToken());
        ChangePostFavoriteDto changePostFavoriteDto = new ChangePostFavoriteDto(id, changePostFavoriteReq.getPost_id(), changePostFavoriteReq.getFavorite());
        PostDetailDto result;
        if(changePostFavoriteDto.getFavorite() == 1){
            result = favoritePostDao.removeFavorite(changePostFavoriteDto.getPost_id(), changePostFavoriteDto.getId());
        } else{
            result = favoritePostDao.setFavorite(changePostFavoriteDto.getPost_id(), changePostFavoriteDto.getId());
        }
        return result;
    }

}
