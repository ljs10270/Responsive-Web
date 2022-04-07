package util;

import java.security.MessageDigest;

public class SHA256 { 
	//회원가입 이후 이메일 인증을 할 때 회원가입 시 입력한 이메일에 해시값을 적용하여 이것을 인증코드로 링크를 타고 인증하게 한다.
	//사용자 비밀번호 같이 중요 개인정보는 반드시 해시화하여야 한다. 관리자라도 알아서는 안된다.
	
	public static String getSHA256(String input) {
		StringBuffer result = new StringBuffer(); //스트링버퍼  생성
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256"); 
			byte[] salt = "솔트값 적용 필수".getBytes(); //salt를 이용함으로서 해시사전을 통한 레인보우 테이블 공격 방어
			digest.reset();
			digest.update(salt); //salt값 적용
			byte[] chars = digest.digest(input.getBytes("UTF-8"));
			
			for(int i = 0; i <chars.length; i++) {//chars를 문자열 형태로 만든다.
				String hex = Integer.toHexString(0xff & chars[i]); //hex값과 해시값을 적용한 chars의 인덱스를 and 연산을 한다. 
				if(hex.length() == 1) //1자리 수인 경우는
					result.append("0"); //뒤에 0을 붙여서 총 2자리수를 가지는 16진수로 만든다.
				result.append(hex); // 뒤에 hex값을 붙인다.
				
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return result.toString(); //해시값 반환
	}
}
