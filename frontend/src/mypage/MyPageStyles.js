import styled from "styled-components";

export const Container = styled.div`
  padding: 8rem 5rem;
  width: auto;
  margin: 0 auto;
  min-height: 100vh;
  overflow-x: hidden;
  background: linear-gradient(to bottom, #fff7d6, #ffecb3);
`;

export const DropdownWrapper = styled.div`
  margin-bottom: 2rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  justify-content: flex-end;

  @media (max-width: 768px) {
    justify-content: center;
    flex-direction: column;
    align-items: flex-start;
  }
`;

export const DropdownLabel = styled.label`
  font-weight: 600;
  font-size: 1rem;
  color: #333;
`;

export const DropdownSelect = styled.select`
  padding: 0.4rem 0.8rem;
  border: 1px solid #ccc;
  border-radius: 6px;
  font-size: 1rem;
  background: #fff;
  color: #333;
  cursor: pointer;

  &:focus {
    outline: none;
    border-color: #fbc02d;
    box-shadow: 0 0 0 2px #fff9c4;
  }
`;

export const StartReadingWrapper = styled.div`
  display: flex;
  justify-content: center;
  margin: 2rem 0;
`;

export const StartReadingButton = styled.button`
  font-family: "Pretendard", "Apple SD Gothic Neo", "Noto Sans KR", sans-serif;
  font-size: 1rem;
  padding: 1rem 2rem;
  background-color: #fdd835;
  color: #333;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    background-color: #ffeb3b;
    transform: select(1);
  }
`;
