import styled from "styled-components";

export const Backdrop = styled.div`
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 10;
`;

export const Modal = styled.div`
  background: #fdf8f3;
  padding: 2.5rem 2.5rem;
  border-radius: 6px;
  min-width: 30rem;
  box-shadow: 0 4px 20px rgba(101, 67, 33, 0.2);
  border: 1px solid #e0d5c0;

  @media (max-width: 480px) {
    min-width: 90%;
    padding: 1.5rem;
  }
`;

export const Field = styled.div`
  margin-bottom: 1.5rem;
`;

export const Input = styled.input`
  width: 100%;
  padding: 0.75rem 0.8rem;
  font-size: 0.8rem;
  border: 1px solid #c8b9a6;
  border-radius: 4px;
  outline: none;
  background: #fffefc;
  transition: border-color 0.2s;

  &:focus {
    border-color: rgb(158, 128, 91);
    background: #fdfaf7;
  }
`;

export const ButtonRow = styled.div`
  display: flex;
  justify-content: center;
  gap: 0.8rem;
  margin-top: 2rem;
`;

export const Button = styled.button`
  padding: 0.6rem 1.4rem;
  font-size: 0.95rem;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;

  &:first-child {
    background: #dcd2c0;
    color: #4b3b2b;

    &:hover {
      background: #c8baa4;
    }
  }

  &:last-child {
    background: #8b5e3c;
    color: white;

    &:hover {
      background: #744a2d;
    }
  }
`;
