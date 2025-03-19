import { useState, useEffect, useContext } from "react";
import { useNavigate } from "react-router-dom";
import {
  HeaderContainer,
  Logo,
  CenterContainer,
  Nav,
  NavLink,
  SlideMenu,
  HamburgerButton,
  LogoutButton,
} from "./HeaderStyle";
import { AuthContext } from "../AuthContext";

function Header() {
  const [isScrolled, setIsScrolled] = useState(false);
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const { accessToken, logout, isLoading } = useContext(AuthContext);
  const navigate = useNavigate();

  useEffect(() => {
    const handleScroll = () => {
      setIsScrolled(window.scrollY > 50);
    };

    window.addEventListener("scroll", handleScroll);
    return () => window.removeEventListener("scroll", handleScroll);
  }, []);

  const toggleMenu = () => {
    setIsMenuOpen((prev) => !prev);
  };

  const handleLogout = async () => {
    await logout();
    alert("로그아웃 되었습니다!");
    navigate("/");
  };

  return (
    <HeaderContainer isScrolled={isScrolled}>
      <Logo to="/">LitHub</Logo>
      <CenterContainer>
        <Nav>
          <NavLink to="#hero">STUDY</NavLink>
          <NavLink to="#features">FORUM</NavLink>
          {!isLoading && accessToken && <NavLink to="/mypage">MYPAGE</NavLink>}
          {!isLoading &&
            (accessToken ? (
              <LogoutButton onClick={handleLogout}>LOGOUT</LogoutButton>
            ) : (
              <NavLink to="/login-register">LOGIN</NavLink>
            ))}
        </Nav>
      </CenterContainer>
      <HamburgerButton isOpen={isMenuOpen} onClick={toggleMenu}>
        <div />
        <div />
        <div />
      </HamburgerButton>
      <SlideMenu isOpen={isMenuOpen}>
        <NavLink to="#features" onClick={toggleMenu}>
          STUDY
        </NavLink>
        <NavLink to="#contact" onClick={toggleMenu}>
          FORUM
        </NavLink>
        {!isLoading && accessToken && (
          <NavLink to="/mypage" onClick={toggleMenu}>
            MYPAGE
          </NavLink>
        )}
        {!isLoading &&
          (accessToken ? (
            <LogoutButton
              as="button"
              onClick={() => {
                handleLogout();
                toggleMenu();
              }}
            >
              LOGOUT
            </LogoutButton>
          ) : (
            <NavLink to="/login-register" onClick={toggleMenu}>
              LOGIN
            </NavLink>
          ))}
      </SlideMenu>
    </HeaderContainer>
  );
}

export default Header;
