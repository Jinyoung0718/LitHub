import googleIcon from "../../assets/google-icon.png";
import naverIcon from "../../assets/naver-icon.png";
import { SocialIcons, SocialIcon, Separator } from "./LoginStyles";

const OAuthLogin = () => {
  const handleOAuth2Login = (provider) => {
    window.location.href = `http://localhost:8080/oauth2/authorization/${provider}`;
  };

  return (
    <>
      <Separator>OR</Separator>
      <SocialIcons>
        <SocialIcon as="button" onClick={() => handleOAuth2Login("google")}>
          <img src={googleIcon} alt="Google 로그인" />
        </SocialIcon>
        <SocialIcon as="button" onClick={() => handleOAuth2Login("kakao")}>
          <img src={naverIcon} alt="Naver 로그인" />
        </SocialIcon>
      </SocialIcons>
    </>
  );
};

export default OAuthLogin;
