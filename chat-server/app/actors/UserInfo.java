package actors;

public class UserInfo implements Cloneable {

    private String userName;
    private String userPicture;
    private String userId;

    public UserInfo() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(final String userName) {
        this.userName = userName;
    }

    public String getUserPicture() {
        return userPicture;
    }

    public void setUserPicture(final String userPicture) {
        this.userPicture = userPicture;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

}
