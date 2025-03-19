import styled from "styled-components";

export const Container = styled.div`
  user-select: none;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(to bottom, #fff7d6, #ffecb3);
`;

export const FormContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 2rem;
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
  align-items: center;
  gap: 1rem;
  margin: 0 auto 2rem;

  button {
    background: none;
    border: none;
    font-size: 1rem;
    font-weight: bold;
    color: #666;
    cursor: pointer;
    transition: color 0.3s;
    padding: 0.5rem 1rem;
    border-radius: 5px;

    &.active {
      color: #ff7043;
    }

    &:hover {
      color: #ff7043;
    }

    &:focus {
      outline: none;
    }
  }
`;

export const EmailInputGroup = styled.div`
  display: flex;
  gap: 1rem;
  width: 27rem;
  margin-bottom: 1.2rem;

  input {
    flex: 2.5;
    padding: 0.8rem;
    border: 1px solid #ffcc80;
    border-radius: 5px;
    font-size: 0.8rem;

    &::placeholder {
      color: #aaa;
    }

    &:focus {
      outline: none;
      border-color: #ff7043;
    }
  }

  button {
    flex: 0.5;
    padding: 0.8rem;
    background: #ff7043;
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    font-size: 0.75rem;
    height: auto;

    &:hover {
      background: #ff5722;
    }
  }
`;

export const InputBlock = styled.div`
  margin-bottom: 1.2rem;

  input {
    width: 25rem;
    padding: 0.8rem;
    border: 1px solid #ffcc80;
    border-radius: 5px;
    font-size: 0.8rem;

    &::placeholder {
      color: #aaa;
    }

    &:focus {
      outline: none;
      border-color: #ff7043;
    }
  }
`;

export const PasswordValidation = styled.div`
  font-size: 0.8rem;
  color: ${(props) => (props.isMatch ? "#4CAF50" : "#F44336")};
  margin-top: 0.5rem;
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
  transition: background 0.3s;

  &:hover {
    background: #ffb347;
  }
`;

export const Separator = styled.div`
  position: relative;
  margin: 1.5rem 0;
  font-size: 0.75rem;
  color: #aaa;

  &::before,
  &::after {
    content: "";
    position: absolute;
    top: 50%;
    width: 45%;
    height: 1px;
    background: #ddd;
  }

  &::before {
    left: 0;
  }

  &::after {
    right: 0;
  }
`;

export const SocialIcons = styled.div`
  display: flex;
  justify-content: center;
  gap: 1rem;
`;

export const SocialIcon = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  width: 3rem;
  height: 3rem;
  border-radius: 50%;
  background: #fff;
  border: 1px solid #ddd;
  color: #444;
  cursor: pointer;

  &:hover {
    color: #ff7043;
    border-color: #ff7043;
  }

  img {
    width: 100%;
    border-radius: 50%;
    height: 100%;
    object-fit: contain;
  }
`;
