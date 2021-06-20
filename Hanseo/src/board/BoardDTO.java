package board;

public class BoardDTO {
	
	String userID;
	int boardID;
	String boardTitle;
	String boardContent;
	String boardDate;
	int boardHit; //조회수
	String boardFile;
	String boardRealFile; //프로필 이미지 파일과 중복이 되면 upload 폴더 인식 못함 즉, 중복 보안을 위해 리얼파일로 옮기면서 파일명 달라지게 할 것 
	String lectureType;
	int likeCount;
	
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
	public String getBoardTitle() {
		return boardTitle;
	}
	public void setBoardTitle(String boardTitle) {
		this.boardTitle = boardTitle;
	}
	public String getBoardContent() {
		return boardContent;
	}
	public void setBoardContent(String boardContent) {
		this.boardContent = boardContent;
	}
	public String getBoardDate() {
		return boardDate;
	}
	public void setBoardDate(String boardDate) {
		this.boardDate = boardDate;
	}
	public int getBoardHit() {
		return boardHit;
	}
	public void setBoardHit(int boardHit) {
		this.boardHit = boardHit;
	}
	public String getBoardFile() {
		return boardFile;
	}
	public void setBoardFile(String boardFile) {
		this.boardFile = boardFile;
	}
	public String getBoardRealFile() {
		return boardRealFile;
	}
	public void setBoardRealFile(String boardRealFile) {
		this.boardRealFile = boardRealFile;
	}
	public String getLectureType() {
		return lectureType;
	}
	public void setLectureType(String lectureType) {
		this.lectureType = lectureType;
	}
	public int getLikeCount() {
		return likeCount;
	}
	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}
}
