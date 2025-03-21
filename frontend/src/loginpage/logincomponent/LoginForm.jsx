import { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { InputBlock, Button } from "./LoginStyles";

const LoginForm = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    email: "",
    password: "",
  });
  const [errorMessage, setErrorMessage] = useState("");

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSignin = async () => {
    try {
      setErrorMessage("");

      await axios.post("/api/auth/basic/login", {
        username: formData.email,
        password: formData.password,
      });

      alert("로그인 성공!");
      navigate("/");
    } catch (error) {
      const message = error.response?.data?.message || "로그인 실패";

      if (message === "이 계정은 삭제되었습니다. 복구하시겠습니까?") {
        if (window.confirm("이 계정은 삭제되었습니다. 복구하시겠습니까?")) {
          await axios.post("/api/auth/restore-user", {
            email: formData.email,
          });

          alert("계정 복구가 완료되었습니다. 다시 로그인해주세요.");
          await handleSignin();
        }
      } else {
        setErrorMessage(message);
      }
    }
  };

  return (
    <form onSubmit={(e) => e.preventDefault()}>
      {errorMessage && <p style={{ color: "red" }}>{errorMessage}</p>}
      <InputBlock>
        <input
          type="email"
          name="email"
          placeholder="이메일"
          value={formData.email}
          onChange={handleChange}
        />
      </InputBlock>
      <InputBlock>
        <input
          type="password"
          name="password"
          placeholder="비밀번호"
          value={formData.password}
          onChange={handleChange}
        />
      </InputBlock>
      <Button type="button" onClick={handleSignin}>
        Sign In
      </Button>
    </form>
  );
};

export default LoginForm;
