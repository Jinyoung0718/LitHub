import styled from "styled-components";

export const Container = styled.div`
  user-select: none;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(to bottom, #fff7d6, #ffecb3);
  padding: 1rem;
`;

export const FormContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 2rem;
  width: 100%;
  max-width: 30rem;
`;

export const Header = styled.h1`
  font-size: 2rem;
  color: #ff7043;
  margin-bottom: 1.5rem;
  text-align: center;
`;

export const InputGroup = styled.div`
  display: flex;
  gap: 0.4rem;
  margin-bottom: 1rem;

  input {
    flex: 3;
    width: 24.6rem;
    padding: 1rem;
    border: 1px solid #ffcc80;
    border-radius: 5px;
    font-size: 0.7rem;

    &::placeholder {
      color: #aaa;
    }

    &:focus {
      outline: none;
      border-color: #ff7043;
    }
  }

  button {
    flex: 0.7;
    padding: 0.5rem;
    background: #ff7043;
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    font-size: 0.85rem;
    transition: background 0.3s;

    &:hover {
      background: #ff5722;
    }

    &:disabled {
      background: #ccc;
      cursor: not-allowed;
    }
  }
`;

export const InputBlock = styled.div`
  width: 100%;
  margin-bottom: 1rem;

  input {
    width: 95%;
    padding: 1rem;
    border: 1px solid #ffcc80;
    border-radius: 5px;
    font-size: 0.7rem;

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
  font-size: 0.85rem;
  margin-top: 0.4rem;
  color: ${(props) => (props.isMatch ? "#4CAF50" : "#F44336")};
  text-align: center;
`;

export const SignupButton = styled.button`
  width: 100%;
  padding: 0.85rem;
  background: #ffcc80;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background 0.3s;
  margin-top: 0.5rem;

  &:hover {
    background: #ffb347;
  }

  &:disabled {
    background: #ccc;
    cursor: not-allowed;
  }
`;

export const ErrorMessage = styled.p`
  color: red;
  font-size: 0.9rem;
  margin-bottom: 1rem;
  text-align: center;
`;
