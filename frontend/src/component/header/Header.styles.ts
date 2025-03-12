import styled from "styled-components";

interface HeaderProps {
  $isScrolled: boolean;
}

export const HeaderContainer = styled.header<HeaderProps>`
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  z-index: 3;
  background: ${(props) =>
    props.$isScrolled
      ? "rgba(240, 231, 206, 0.95)"
      : "rgba(255, 241, 178, 0.9)"};
  color: #333;
  transition: background 0.4s ease-in-out, box-shadow 0.2s ease-in-out;
  padding: 1rem 2rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
`;

export const Logo = styled.span`
  font-size: 1.8rem;
  font-weight: 700;
  color: #f54e42;
  text-decoration: none;
  font-family: "Roboto", sans-serif;
`;

export const CenterContainer = styled.div`
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 2rem;
`;

export const Nav = styled.nav`
  display: flex;
  gap: 2rem;
`;

export const NavItem = styled.span`
  font-size: 0.93rem;
  font-weight: bold;
  text-decoration: none;
  color: #333;
  cursor: pointer;
  position: relative;

  &:hover {
    color: #d53f8c;
  }

  &::after {
    content: "";
    position: absolute;
    left: 0;
    bottom: -3px;
    width: 100%;
    height: 2px;
    background-color: #d53f8c;
    transform: scaleX(0);
    transform-origin: right;
    transition: transform 0.3s ease-in-out;
  }

  &:hover::after {
    transform: scaleX(1);
    transform-origin: left;
  }
`;
