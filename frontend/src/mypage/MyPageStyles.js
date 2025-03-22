import styled from "styled-components";

export const Container = styled.div`
  padding: 8rem 5rem;
  width: auto;
  margin: 0 auto;
  min-height: 100vh;
  background: linear-gradient(to bottom, #fff7d6, #ffecb3);
`;

export const ProfileSection = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 60px;
  padding: 2rem;
`;

export const ProfileImage = styled.img`
  width: 140px;
  height: 140px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid #ffecb3;
  margin-bottom: 20px;
`;

export const InfoBox = styled.div`
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 12px 20px;
  width: 100%;
  max-width: 500px;
`;

export const Label = styled.span`
  font-weight: 600;
  font-size: 14px;
  color: #666;
  text-align: right;
`;

export const Value = styled.span`
  font-size: 16px;
  font-weight: 500;
  color: #333;
`;

export const StatsBox = styled.div`
  margin-top: 30px;
  padding: 20px;
  background-color: #f4f4f4;
  border-radius: 8px;
`;
