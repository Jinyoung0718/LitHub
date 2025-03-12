import styled from "styled-components";
import { motion } from "framer-motion";

export const HeroContainer = styled(motion.section)`
  position: relative;
  user-select: none;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #ffe761, #fff2b3);
  color: #222;
  text-align: center;
  font-family: "Noto Sans KR", sans-serif;
  overflow: hidden;
`;

export const AnimatedShapes = styled.div`
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;

  .circle {
    position: absolute;
    background-color: #f56565;
    border-radius: 50%;
    animation: floatCircle 8s infinite cubic-bezier(0.4, 0, 0.2, 1);

    &.large {
      top: 15%;
      left: 10%;
      width: 120px;
      height: 120px;
    }

    &.small {
      top: 70%;
      left: 60%;
      width: 60px;
      height: 60px;
      animation-duration: 6s;
    }
  }

  .rectangle {
    position: absolute;
    top: 50%;
    left: 75%;
    width: 100px;
    height: 50px;
    background-color: #4299e1;
    animation: rotateRectangle 10s infinite ease-in-out;
  }

  .triangle {
    position: absolute;
    top: 80%;
    left: 30%;
    width: 0;
    height: 0;
    border-left: 50px solid transparent;
    border-right: 50px solid transparent;
    border-bottom: 100px solid #f6ad55;
    animation: zigzagTriangle 7s infinite ease-in-out;
  }

  .square {
    position: absolute;
    top: 25%;
    left: 70%;
    width: 70px;
    height: 70px;
    background-color: #68d391;
    animation: scaleSquare 5s infinite cubic-bezier(0.68, -0.55, 0.27, 1.55);
  }

  .oval {
    position: absolute;
    top: 40%;
    left: 20%;
    width: 80px;
    height: 40px;
    background-color: #ed64a6;
    border-radius: 50%;
    animation: driftOval 9s infinite ease-in-out;
  }

  .hexagon {
    position: absolute;
    top: 10%;
    left: 50%;
    width: 80px;
    height: 46px;
    background-color: #805ad5;
    clip-path: polygon(50% 0%, 100% 25%, 100% 75%, 50% 100%, 0% 75%, 0% 25%);
    animation: bounceHexagon 6s infinite cubic-bezier(0.4, 0, 0.6, 1);
  }

  @keyframes floatCircle {
    0%,
    100% {
      transform: translateY(0);
    }
    50% {
      transform: translateY(-20px);
    }
  }

  @keyframes rotateRectangle {
    0% {
      transform: rotate(0deg);
    }
    100% {
      transform: rotate(360deg);
    }
  }

  @keyframes zigzagTriangle {
    0% {
      transform: translateX(0) translateY(0);
    }
    25% {
      transform: translateX(20px) translateY(-10px);
    }
    50% {
      transform: translateX(-20px) translateY(10px);
    }
    75% {
      transform: translateX(10px) translateY(-10px);
    }
    100% {
      transform: translateX(0) translateY(0);
    }
  }

  @keyframes scaleSquare {
    0%,
    100% {
      transform: scale(1);
    }
    50% {
      transform: scale(1.2);
    }
  }

  @keyframes driftOval {
    0%,
    100% {
      transform: translateX(0);
    }
    50% {
      transform: translateX(30px);
    }
  }

  @keyframes bounceHexagon {
    0%,
    100% {
      transform: translateY(0);
    }
    50% {
      transform: translateY(-20px);
    }
  }
`;

export const Content = styled.div`
  position: relative;
  z-index: 1;
`;

export const Title = styled(motion.h1)`
  font-size: 4.5rem;
  font-weight: bold;
  margin-bottom: 1rem;
  color: #2d3748;
  font-family: "Roboto", sans-serif;

  @media (max-width: 768px) {
    font-size: 2.5rem;
  }
`;

export const Subtitle = styled(motion.p)`
  font-size: 1rem;
  font-weight: bold;
  color: #4a5568;
  font-family: "Roboto", sans-serif;

  @media (max-width: 768px) {
    font-size: 0.8rem;
  }
`;
