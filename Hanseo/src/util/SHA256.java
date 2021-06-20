package util;

import java.security.MessageDigest;

public class SHA256 { //Secure Hash Algorithm
	//회원가입 이후 이메일 인증을 할 때 회원가입 시 입력한 이메일에 해시값을 적용하여 이것을 인증코드로 링크를 타고 인증하게 한다.
	public static String getSHA256(String input) { // input은 사용자의 이메일이 들어온다.
		StringBuffer result = new StringBuffer(); //스트링버퍼  생성
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256"); //사용자가 입력한 이메일에 SHA-256 알고리즘을 적용할 수 있도록 함
			byte[] salt = "This is Salt".getBytes(); //salt를 이용함으로서 버퍼에 접근하는 해킹 방어
			digest.reset();
			digest.update(salt); //salt값 적용, 실제 해시에 적용하는 값이 아니다.
			byte[] chars = digest.digest(input.getBytes("UTF-8"));//실제 해시를 적용한 값을 chars에 담는다.
			
			for(int i = 0; i <chars.length; i++) {//chars를 문자열 형태로 만든다.
				String hex = Integer.toHexString(0xff & chars[i]); //hex값과 해시값을 적용한 chars의 인덱스를 and연산을 한다. 
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
