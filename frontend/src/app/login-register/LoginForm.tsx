"use client";

import { useState } from "react";
import { InputBlock, Button } from "./LoginRegister.styles";
import { useAuth } from "../hooks/useAuth";

export default function LoginForm() {
  const { login } = useAuth();

  const [formData, setFormData] = useState({
    email: "",
    password: "",
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    await login(formData.email, formData.password);
  };

  return (
    <form onSubmit={handleSubmit}>
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
      <Button type="submit">Sign In</Button>
    </form>
  );
}
