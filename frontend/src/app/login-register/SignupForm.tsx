"use client";

import { useState } from "react";
import { InputBlock, Button } from "./LoginRegister.styles";
import { useAuth } from "../hooks/useAuth";
import EmailVerification from "./EmailVerification";
import NicknameVerification from "./NicknameVerification";

export default function SignupForm() {
  const { checkNickname, signup } = useAuth();

  const [formData, setFormData] = useState({
    email: "",
    verificationCode: "",
    nickname: "",
    password: "",
  });

  const [nicknameVerified, setNicknameVerified] = useState<boolean | null>(
    null
  );

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });

    if (name === "nickname") {
      setNicknameVerified(null); // 닉네임 변경 시 다시 검증 필요하도록 초기화
    }
  };

  const handleCheckNickname = async () => {
    const isAvailable = await checkNickname(formData.nickname);
    setNicknameVerified(isAvailable);
  };

  const handleSignup = async () => {
    if (!nicknameVerified) {
      alert("닉네임 중복 확인이 필요합니다.");
      return;
    }
    try {
      await signup(formData.nickname, formData.email, formData.password);
      alert("회원가입 성공!");
    } catch (error) {
      console.error("회원가입 실패:", error);
      alert("회원가입에 실패했습니다.");
    }
  };

  return (
    <form onSubmit={(e) => e.preventDefault()}>
      <EmailVerification email={formData.email} setFormData={setFormData} />

      <NicknameVerification
        nickname={formData.nickname}
        nicknameVerified={nicknameVerified}
        setNicknameVerified={setNicknameVerified}
        handleChange={handleChange}
        handleCheckNickname={handleCheckNickname}
      />

      <InputBlock>
        <input
          type="password"
          name="password"
          placeholder="비밀번호"
          value={formData.password}
          onChange={handleChange}
        />
      </InputBlock>

      <Button type="button" onClick={handleSignup}>
        Sign Up
      </Button>
    </form>
  );
}
