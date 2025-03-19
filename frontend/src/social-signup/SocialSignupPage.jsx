import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import {
  Container,
  FormContainer,
  Header,
  InputGroup,
  InputBlock,
  PasswordValidation,
  SignupButton,
  ErrorMessage,
} from "./SocialSignupPage.js";

const SocialSignupPage = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    nickname: "",
    password: "",
    confirmPassword: "",
  });

  const [errorMessage, setErrorMessage] = useState("");
  const [isNicknameChecked, setIsNicknameChecked] = useState(false);
  const [isCheckingNickname, setIsCheckingNickname] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    checkTempToken();
  }, []);

  const checkTempToken = async () => {
    try {
      const requestUrl = "http://localhost:8080/api/info/temp-check";
      const response = await axios.get(requestUrl, {
        withCredentials: true,
      });

      if (!response.data.success) {
        throw new Error("세션이 만료되었습니다.");
      }
    } catch (error) {
      alert("세션이 만료되었습니다. 다시 로그인해주세요.");
      navigate("/login-register");
    }
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });

    if (e.target.name === "nickname") {
      setIsNicknameChecked(false);
    }
  };

  const isPasswordMatch =
    formData.password.length > 0 &&
    formData.confirmPassword.length > 0 &&
    formData.password === formData.confirmPassword;

  const handleCheckNickname = async () => {
    if (isCheckingNickname || isNicknameChecked) return;

    try {
      setErrorMessage("");
      setIsCheckingNickname(true);
      await axios.post("/api/auth/check-nickname", {
        nickName: formData.nickname,
      });

      alert("사용 가능한 닉네임입니다!");
      setIsNicknameChecked(true);
    } catch (error) {
      setErrorMessage(error.response?.data?.message || "닉네임 중복 확인 실패");
    } finally {
      setIsCheckingNickname(false);
    }
  };

  const handleSignup = async () => {
    if (!isPasswordMatch || !isNicknameChecked) return;

    try {
      setErrorMessage("");
      setIsSubmitting(true);

      await axios.post("/api/auth/social-signup", {
        nickName: formData.nickname,
        userPassword: formData.password,
      });

      alert("회원가입 성공!");
      navigate("/");
    } catch (error) {
      setErrorMessage(error.response?.data?.message || "오류 발생");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Container>
      <FormContainer>
        <Header>LitHub</Header>
        {errorMessage && <ErrorMessage>{errorMessage}</ErrorMessage>}

        <InputGroup>
          <input
            type="text"
            name="nickname"
            placeholder="사용자 닉네임"
            value={formData.nickname}
            onChange={handleChange}
          />
          <button
            type="button"
            onClick={handleCheckNickname}
            disabled={isCheckingNickname || isNicknameChecked}
          >
            Check
          </button>
        </InputGroup>

        <InputBlock>
          <input
            type="password"
            name="password"
            placeholder="비밀번호"
            value={formData.password}
            onChange={handleChange}
          />
        </InputBlock>

        <InputBlock>
          <input
            type="password"
            name="confirmPassword"
            placeholder="비밀번호 확인"
            value={formData.confirmPassword}
            onChange={handleChange}
          />
          {formData.confirmPassword.length > 0 && (
            <PasswordValidation isMatch={isPasswordMatch}>
              {isPasswordMatch
                ? "비밀번호가 일치합니다"
                : "비밀번호가 일치하지 않습니다"}
            </PasswordValidation>
          )}
        </InputBlock>

        <SignupButton
          type="button"
          onClick={handleSignup}
          disabled={!isPasswordMatch || !isNicknameChecked || isSubmitting}
        >
          Social - Sign Up
        </SignupButton>
      </FormContainer>
    </Container>
  );
};

export default SocialSignupPage;
