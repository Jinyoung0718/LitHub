import styled from "styled-components";

export const HeatmapContainer = styled.div`
  margin-top: 1rem;
`;

export const LegendContainer = styled.div`
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 1rem;
  font-size: 0.9rem;
`;

export const LegendBoxWrapper = styled.div`
  display: flex;
  align-items: center;
  gap: 4px;
`;

export const ColorBlock = styled.div`
  width: 14px;
  height: 14px;
  border-radius: 3px;
  background-color: ${(props) => props.color};
`;
