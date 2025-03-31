import styled from "styled-components";

export const ProfileContainer = styled.div`
  padding: 2rem;
  max-width: 100rem;
  margin: 0 auto 2rem;
`;

export const ProfileHeader = styled.div`
  text-align: center;
  margin-bottom: 1.5rem;
`;

export const ProfileAvatarWrapper = styled.div`
  position: relative;
  display: inline-block;
`;

export const ProfileAvatar = styled.img`
  width: 8rem;
  height: 8rem;
  border-radius: 50%;
  object-fit: cover;
  margin-bottom: 0.75rem;
  border: 1px solid;
  transition: box-shadow 0.3s ease;

  &.bronze {
    border-color: #cd7f32;
    box-shadow: 0 0 10px #cd7f32aa;
    animation: glow-bronze 2s ease-in-out infinite alternate;
  }

  &.silver {
    border-color: #c0c0c0;
    box-shadow: 0 0 10px #c0c0c0aa;
    animation: glow-silver 2s ease-in-out infinite alternate;
  }

  &.gold {
    border-color: #ffd700;
    box-shadow: 0 0 12px #ffd700aa;
    animation: glow-gold 2s ease-in-out infinite alternate;
  }

  &.platinum {
    border-color: #00bcd4;
    box-shadow: 0 0 12px #00bcd4aa;
    animation: glow-plat 2s ease-in-out infinite alternate;
  }

  @keyframes glow-bronze {
    from {
      box-shadow: 0 0 8px #cd7f32aa;
    }
    to {
      box-shadow: 0 0 16px #cd7f32ff;
    }
  }

  @keyframes glow-silver {
    from {
      box-shadow: 0 0 8px #c0c0c0aa;
    }
    to {
      box-shadow: 0 0 16px #ffffff;
    }
  }

  @keyframes glow-gold {
    from {
      box-shadow: 0 0 8px #ffd700aa;
    }
    to {
      box-shadow: 0 0 16px #fff176;
    }
  }

  @keyframes glow-plat {
    from {
      box-shadow: 0 0 8px #00bcd4aa;
    }
    to {
      box-shadow: 0 0 16px #b2ebf2;
    }
  }
`;

export const DropdownWrapper = styled.div`
  position: absolute;
  top: 0;
  right: -10px;
`;

export const DropdownToggle = styled.button`
  background: transparent;
  border: none;
  font-size: 1.2rem;
  cursor: pointer;
  color: #555;
`;

export const DropdownMenu = styled.div`
  position: absolute;
  top: 25px;
  left: 0;
  background: white;
  border: 1px solid #ccc;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  border-radius: 6px;
  z-index: 1000;
  min-width: 150px;
`;

export const DropdownItem = styled.div`
  padding: 10px 14px;
  cursor: pointer;
  font-size: 0.9rem;
  display: flex;
  align-items: center;
  color: #333;

  &:hover {
    background-color: #f5f5f5;
  }
`;

export const Nickname = styled.div`
  font-size: 1.8rem;
  font-weight: 600;
  color: #222;
  margin-bottom: 0.5rem;
`;

export const NicknameRow = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.4rem;
  margin-bottom: 1rem;
`;

export const TierBadge = styled.span`
  padding: 4px 10px;
  font-size: 0.8rem;
  font-weight: 600;
  border-radius: 12px;
  text-transform: uppercase;
  display: inline-block;

  &.bronze {
    background-color: #cd7f32;
    color: white;
  }

  &.silver {
    background-color: #c0c0c0;
    color: black;
  }

  &.gold {
    background-color: #ffd700;
    color: black;
  }

  &.platinum {
    background-color: #00bcd4;
    color: white;
  }
`;

export const InfoList = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
`;

export const InfoItem = styled.div`
  display: flex;
  gap: 10px;
  align-items: center;
  font-size: 1rem;
`;

export const Label = styled.div`
  font-weight: bold;
  color: #555;
  min-width: 80px;
  text-align: right;
`;

export const Value = styled.div`
  color: #222;
`;

export const PointBarContainer = styled.div`
  margin-top: 0.5rem;
  width: 100%;
  padding: 0 1rem;
  box-sizing: border-box;
  margin: 0 auto;
`;

export const ProgressBar = styled.div`
  position: relative;
  height: 1.8rem;
  width: 90%;
  background-color: #eee;
  border-radius: 6px;
  overflow: hidden;
  margin: 0 auto;

  @media (max-width: 480px) {
    width: 80%;
  }

  @media (min-width: 481px) and (max-width: 768px) {
    width: 70%;
  }

  @media (min-width: 769px) {
    width: 80%;
  }
`;

export const ProgressFill = styled.div`
  height: 100%;
  background: linear-gradient(90deg, #fdd835, #fbc02d);
  transition: width 0.3s ease-in-out;
`;

export const ProgressTextInBar = styled.div`
  position: absolute;
  width: 100%;
  text-align: center;
  font-size: 0.85rem;
  color: #333;
  top: 50%;
  transform: translateY(-50%);
  z-index: 1;
  pointer-events: none;
`;
