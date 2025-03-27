package faang.school.postservice.controller.album;

public class AlbumConstant {

    public static final String BASE_PATH = "/album";
    public static final String DELETE_ALBUM = BASE_PATH + "/delete_album";
    public static final String FIND_FAVORITE_ALBUMS = BASE_PATH + "/find_favorite_albums";
    public static final String DELETE_ALBUM_FROM_FAVORITE = BASE_PATH + "/delete_album_from_favorites";
    public static final String ADD_ALBUM_TO_FAVORITE = BASE_PATH + "/add_album_to_favorites";
    public static final String FIND_POSTS = BASE_PATH + "/find_posts_by_album_id/{albumId}";
    public static final String FIND_BY_AUTHOR_ID = BASE_PATH + "/find_by_author_id";
    public static final String FIND_BY_ALBUM_ID = BASE_PATH + "/find_by_id/{albumId}";
    public static final String SHOW_ALL = BASE_PATH + "/show_all";
    public static final String ADD_POST = BASE_PATH + "/add_post";
    public static final String CREATE_ALBUM = BASE_PATH + "/create_album";
    public static final String DELETE_POST = BASE_PATH + "/delete_post";
}
