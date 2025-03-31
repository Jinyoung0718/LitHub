import styled from "styled-components";

export const Wrapper = styled.div`
  position: absolute;
  top: 5rem;
  right: 40px;
`;

export const ToggleButton = styled.button`
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1rem;
`;

export const Menu = styled.div`
  position: absolute;
  top: 2rem;
  right: 0;
  width: 8rem;
  background: white;
  border: 1px solid #ccc;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  border-radius: 6px;
`;

export const Item = styled.div`
  padding: 10px 14px;
  cursor: pointer;
  font-size: 0.8rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;

  &:hover {
    background-color: #f5f5f5;
  }
`;
