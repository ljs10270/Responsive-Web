package reply;

public class ReplyDTO {
	private String userID;
	private int boardID;
	private String replyContent;
	private String replyDate;
	private int replyGroup;
	private int replySequence;
	private int replyLevel;
	private int likeCount;
	private int replyAvailable;
	
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
	public String getReplyContent() {
		return replyContent;
	}
	public void setReplyContent(String replyContent) {
		this.replyContent = replyContent;
	}
	public String getReplyDate() {
		return replyDate;
	}
	public void setReplyDate(String replyDate) {
		this.replyDate = replyDate;
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
	public int getReplyLevel() {
		return replyLevel;
	}
	public void setReplyLevel(int replyLevel) {
		this.replyLevel = replyLevel;
	}
	public int getLikeCount() {
		return likeCount;
	}
	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}
	public int getReplyAvailable() {
		return replyAvailable;
	}
	public void setReplyAvailable(int replyAvailable) {
		this.replyAvailable = replyAvailable;
	}
}
