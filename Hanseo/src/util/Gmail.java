package util;

import javax.mail.Authenticator;

import javax.mail.PasswordAuthentication;

public class Gmail extends Authenticator { //gmail smt를 이용하기 위해 계정 정보가 들어가는 클래스
//Authenticatior는 메일 인증을 도와준다.
	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication("~~~~~@gmail.com", "pw"); //사용자에게 메일을 전송할 즉 관리자의 구글 아이디와 비밀번호를 넣는다.
		//로컬인 이곳에서 서버로 관리자 구글 계정으로 들어가 사용자에게 메일을 보낼 수 있다.
	}
}
