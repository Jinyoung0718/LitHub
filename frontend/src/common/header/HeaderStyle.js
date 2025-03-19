import styled from "styled-components";
import { Link } from "react-router-dom";

export const HeaderContainer = styled.header`
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  z-index: 3;
  background: ${(props) =>
    props.isScrolled
      ? "rgba(240, 231, 206, 0.95)"
      : "rgba(255, 241, 178, 0.9)"};
  color: #333;
  transition: background 0.4s ease-in-out, box-shadow 0.2s ease-in-out;
  padding: 1rem 2rem;
  display: flex;
  align-items: center;
  justify-content: space-between;

  @media (max-width: 1024px) {
    padding: 0.8rem 1.5rem;
  }

  @media (max-width: 768px) {
    padding: 0.6rem 1rem;
  }

  @media (max-width: 480px) {
    padding: 0.5rem 0.8rem;
  }
`;

export const Logo = styled(Link)`
  font-size: 1.8rem;
  font-weight: 700;
  color: #f54e42;
  text-decoration: none;
  letter-spacing: 0.1rem;
  font-family: "Roboto", sans-serif;

  @media (max-width: 1024px) {
    font-size: 1.6rem;
  }

  @media (max-width: 768px) {
    font-size: 1.4rem;
  }

  @media (max-width: 480px) {
    font-size: 1.2rem;
  }
`;

export const CenterContainer = styled.div`
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 2rem;

  @media (max-width: 1024px) {
    display: none;
  }
`;

export const Nav = styled.nav`
  display: flex;
  margin-right: 5rem;
  gap: 2rem;
`;

export const NavLink = styled(Link)`
  font-size: 0.93rem;
  font-weight: bold;
  text-decoration: none;
  color: #333;
  position: relative;

  &::after {
    content: "";
    position: absolute;
    left: 0;
    bottom: 0;
    width: 0;
    height: 2px;
    background: #f39c12;
    transition: width 0.3s ease-in-out;
  }

  &:hover {
    color: #d53f8c;
  }

  &:hover::after {
    width: 100%;
  }
`;

// 로그아웃 버튼도 NavLink 스타일과 동일하게 적용
export const LogoutButton = styled(NavLink).attrs({ as: "button" })`
  all: unset;
  font-size: 0.93rem;
  font-weight: bold;
  color: #333;
  cursor: pointer;
  position: relative;

  &::after {
    content: "";
    position: absolute;
    left: 0;
    bottom: 0;
    width: 0;
    height: 2px;
    background: #f39c12;
    transition: width 0.3s ease-in-out;
  }

  &:hover {
    color: #d53f8c;
  }

  &:hover::after {
    width: 100%;
  }
`;

export const SlideMenu = styled.div`
  position: fixed;
  top: 0;
  right: 0;
  width: 70%;
  height: 100%;
  background: rgba(255, 223, 105, 0.95);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  transform: ${(props) =>
    props.isOpen ? "translateX(0)" : "translateX(100%)"};
  transition: transform 0.5s ease-in-out;
  z-index: 999;
  gap: 1.5rem;

  a,
  button {
    font-size: 1.3rem;
    font-weight: bold;
    color: #333;
    text-decoration: none;
    padding: 0.8rem 0;
    transition: color 0.3s ease-in-out;

    &:hover {
      color: #d53f8c;
    }
  }
`;

export const HamburgerButton = styled.div`
  display: none;

  @media (max-width: 1024px) {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    position: absolute;
    right: 3rem;
    top: 0.8rem;
    width: 2rem;
    height: 1.5rem;
    cursor: pointer;
    z-index: 1000;

    div {
      width: 100%;
      height: 0.2rem;
      background: #333;
      border-radius: 5px;
      transition: all 0.3s ease-in-out;

      &:nth-child(1) {
        transform: ${(props) => (props.isOpen ? "rotate(45deg)" : "none")};
      }
      &:nth-child(2) {
        opacity: ${(props) => (props.isOpen ? 0 : 1)};
      }
      &:nth-child(3) {
        transform: ${(props) => (props.isOpen ? "rotate(-45deg)" : "none")};
      }
    }
  }
`;
