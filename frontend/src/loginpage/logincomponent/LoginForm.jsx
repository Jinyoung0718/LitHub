import { useContext, useState } from "react";
import { AuthContext } from "../../common/AuthContext";
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
      setErrorMessage(error.response?.data?.message || "로그인 실패");
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
