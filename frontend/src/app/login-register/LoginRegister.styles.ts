import styled from "styled-components";
import { motion } from "framer-motion";

export const Container = styled.div`
  user-select: none;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(45deg, #fff7d6, #ffecb3, #ffd699);
  overflow: hidden;
`;

export const FormContainer = styled(motion.div)`
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 2rem;
  margin-top: 3rem;
  text-align: center;
`;

export const Header = styled.h1`
  font-size: 2rem;
  color: #ff7043;
  margin-bottom: 1.5rem;
`;

export const LinkList = styled.div`
  display: flex;
  justify-content: center;
  gap: 1rem;
  margin-bottom: 1.5rem;

  button {
    background: none;
    border: none;
    font-size: 1rem;
    font-weight: bold;
    cursor: pointer;
    padding: 0.5rem 1rem;
    border-radius: 5px;
    transition: color 0.3s ease-in-out, transform 0.2s;

    &.active {
      color: #ff7043;
    }

    &:hover {
      color: #ff7043;
      transform: scale(1.1);
    }

    &:focus {
      outline: none;
    }
  }
`;

export const EmailInputGroup = styled.div<{ isSuccess?: boolean | null }>`
  display: flex;
  align-items: center;
  width: 100%;
  position: relative;
  margin-bottom: 1.2rem;

  input {
    flex-grow: 1;
    padding: 0.8rem;
    width: 25rem;
    border: 2px solid
      ${({ isSuccess }) =>
        isSuccess === true ? "green" : isSuccess === false ? "red" : "#ffcc80"};
    border-radius: 5px;
    font-size: 0.9rem;
    transition: border-color 0.3s ease-in-out;

    &:disabled {
      background: #f0f0f0;
      cursor: not-allowed;
    }
  }

  button {
    position: absolute;
    right: 10px;
    background: none;
    border: none;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;

    svg {
      width: 18px;
      height: 18px;
      color: ${({ isSuccess }) =>
        isSuccess ? "green" : isSuccess === false ? "red" : "#ff7043"};
    }
  }
`;

export const InputBlock = styled.div<{ isSuccess?: boolean | null }>`
  display: flex;
  align-items: center;
  width: 25rem;
  position: relative;
  margin-bottom: 1.2rem;

  input {
    flex-grow: 1;
    width: 100%;
    padding: 0.8rem;
    border: 2px solid
      ${({ isSuccess }) =>
        isSuccess === true ? "green" : isSuccess === false ? "red" : "#ffcc80"};
    border-radius: 5px;
    font-size: 0.9rem;
    transition: border-color 0.3s ease-in-out;

    &:disabled {
      background: #f0f0f0;
      cursor: not-allowed;
    }
  }

  button {
    position: absolute;
    right: 10px;
    background: none;
    border: none;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;

    svg {
      width: 18px;
      height: 18px;
      color: ${({ isSuccess }) =>
        isSuccess ? "green" : isSuccess === false ? "red" : "#ff7043"};
    }
  }
`;

export const ValidationIcon = styled.button`
  background: none;
  border: none;
  cursor: pointer;
  position: absolute;
  right: 10px;
  display: flex;
  align-items: center;
  justify-content: center;

  svg {
    width: 18px;
    height: 18px;
  }
`;

export const Separator = styled.div`
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  margin: 1.8rem 0;
  font-size: 0.8rem;
  color: #aaa;
  text-transform: uppercase;

  &::before,
  &::after {
    content: "";
    flex-grow: 1;
    height: 1px;
    background: #ddd;
    margin: 0 1rem;
  }
`;

export const Button = styled.button`
  width: 85%;
  padding: 0.8rem;
  margin-top: 2rem;
  border: none;
  border-radius: 5px;
  background: #ffcc80;
  color: white;
  font-size: 1rem;
  cursor: pointer;
  transition: background 0.3s, transform 0.2s;

  &:hover {
    background: #ffb347;
  }
`;

export const SocialIcons = styled.div`
  display: flex;
  justify-content: center;
  gap: 2rem;
`;

export const SocialIcon = styled.button`
  background: none;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 3rem;
  height: 3rem;
  border-radius: 50%;
  background: white;
  border: 1px solid #ddd;
  transition: all 0.3s ease-in-out;

  &:hover {
    transform: scale(1.1);
    border-color: #ff7043;
  }

  &:active {
    transform: scale(0.95);
  }
`;
