package likey;

public class ReplyCommentLikeyDTO {
	private String userID;
	private int boardID;
	private int replyGroup;
	private int replySequence;
	private String userIP;
	
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public int getBoardID() {
		return boardID;
	}
	public void setBoardID(int boardID) {
		this.boardID = boardID;
	}
	public int getReplyGroup() {
		return replyGroup;
	}
	public void setReplyGroup(int replyGroup) {
		this.replyGroup = replyGroup;
	}
	public int getReplySequence() {
		return replySequence;
	}
	public void setReplySequence(int replySequence) {
		this.replySequence = replySequence;
	}
	public String getUserIP() {
		return userIP;
	}
	public void setUserIP(String userIP) {
		this.userIP = userIP;
	}
	
}
