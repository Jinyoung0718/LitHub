import styled, { keyframes } from "styled-components";

const fadeIn = keyframes`
  from { opacity: 0; transform: scale(0.95); }
  to { opacity: 1; transform: scale(1); }
`;

export const ModalOverlay = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: #f4a640;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
`;

export const ModalContent = styled.div`
  animation: ${fadeIn} 0.3s ease-out;
  text-align: center;
  color: #222;
`;

export const TimerCircle = styled.div`
  position: relative;
  width: 20rem;
  height: 20rem;
  border-radius: 50%;
  background-color: #111;
  margin: 5rem auto;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 3.2rem;
  color: #fff;
`;

export const ProgressRing = styled.svg`
  position: absolute;
  top: 0;
  left: 0;
  transform: rotate(-90deg);
  width: 320px;
  height: 320px;

  circle {
    transition: stroke-dashoffset 1s linear;
    transform-origin: center;
  }
`;

export const TickMarks = styled.g`
  line {
    stroke: white;
    stroke-width: 2;
  }
`;

export const ButtonGroup = styled.div`
  display: flex;
  justify-content: center;
  gap: 1rem;
  flex-wrap: wrap;
`;

export const ActionButton = styled.button`
  padding: 0.7rem 1.4rem;
  background: #111;
  color: white;
  font-size: 1rem;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: bold;
  transition: all 0.3s ease;

  &:hover {
    background: #333;
    transform: scale(1.05);
  }

  &:active {
    transform: scale(0.95);
  }
`;
