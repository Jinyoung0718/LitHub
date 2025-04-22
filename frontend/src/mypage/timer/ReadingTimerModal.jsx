import React, { useEffect, useState, useRef } from "react";
import {
  ModalOverlay,
  ModalContent,
  TimerCircle,
  ProgressRing,
  TickMarks,
  ButtonGroup,
  ActionButton,
} from "./ReadingTimerStyles";

const ReadingTimerModal = ({ onClose, onSave }) => {
  const [seconds, setSeconds] = useState(0);
  const [isRunning, setIsRunning] = useState(false);
  const intervalRef = useRef(null);

  const radius = 150;
  const strokeWidth = 15;
  const circumference = 2 * Math.PI * radius;

  useEffect(() => {
    if (isRunning) {
      intervalRef.current = setInterval(() => {
        setSeconds((prev) => prev + 1);
      }, 1000);
    } else {
      clearInterval(intervalRef.current);
    }
    return () => clearInterval(intervalRef.current);
  }, [isRunning]);

  const handleSave = () => {
    const minutes = Math.floor(seconds / 60);
    if (minutes < 1) {
      alert("1분 이상부터 저장할 수 있습니다.");
      return;
    }
    onSave(minutes);
    onClose();
  };

  const handleReset = () => {
    setIsRunning(false);
    setSeconds(0);
  };

  const formatTime = () => {
    const mins = String(Math.floor(seconds / 60)).padStart(2, "0");
    const secs = String(seconds % 60).padStart(2, "0");
    return `${mins}:${secs}`;
  };

  const renderTicks = () => {
    const ticks = [];
    const tickRadius = 142;
    const tickLength = 5;
    for (let i = 0; i < 60; i++) {
      const angle = i * 6 * (Math.PI / 180);
      const x1 = 160 + tickRadius * Math.cos(angle);
      const y1 = 160 + tickRadius * Math.sin(angle);
      const x2 = 160 + (tickRadius - tickLength) * Math.cos(angle);
      const y2 = 160 + (tickRadius - tickLength) * Math.sin(angle);
      ticks.push(<line key={i} x1={x1} y1={y1} x2={x2} y2={y2} />);
    }
    return ticks;
  };

  const progress = (seconds % 60) / 60;

  return (
    <ModalOverlay>
      <ModalContent>
        <TimerCircle>
          <ProgressRing viewBox="0 0 320 320">
            <circle
              r={radius}
              cx="160"
              cy="160"
              fill="transparent"
              stroke="#ffa000"
              strokeWidth={strokeWidth}
              strokeDasharray={circumference}
              strokeDashoffset={circumference - progress * circumference}
            />
            <TickMarks>{renderTicks()}</TickMarks>
          </ProgressRing>
          {formatTime()}
        </TimerCircle>

        <ButtonGroup>
          <ActionButton onClick={handleReset}>초기화</ActionButton>
          <ActionButton onClick={() => setIsRunning(!isRunning)}>
            {isRunning ? "일시정지" : "시작"}
          </ActionButton>
          <ActionButton onClick={handleSave}>저장</ActionButton>
          <ActionButton onClick={onClose}>닫기</ActionButton>
        </ButtonGroup>
      </ModalContent>
    </ModalOverlay>
  );
};

export default ReadingTimerModal;
