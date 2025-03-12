"use client";

import { useState, useEffect } from "react";
import Link from "next/link";
import {
  HeaderContainer,
  Logo,
  CenterContainer,
  Nav,
  NavItem,
} from "./Header.styles";

function Header() {
  const [isScrolled, setIsScrolled] = useState(false);

  useEffect(() => {
    const handleScroll = () => {
      setIsScrolled(window.scrollY > 50);
    };

    window.addEventListener("scroll", handleScroll);
    return () => window.removeEventListener("scroll", handleScroll);
  }, []);

  return (
    <HeaderContainer $isScrolled={isScrolled}>
      <Link href="/" passHref>
        <Logo>LitHub</Logo>
      </Link>
      <CenterContainer>
        <Nav>
          <Link href="#hero" passHref>
            <NavItem>STUDY</NavItem>
          </Link>
          <Link href="#features" passHref>
            <NavItem>FORUM</NavItem>
          </Link>
          <Link href="/login-register" passHref>
            <NavItem>LOGIN</NavItem>
          </Link>
        </Nav>
      </CenterContainer>
    </HeaderContainer>
  );
}

export default Header;
