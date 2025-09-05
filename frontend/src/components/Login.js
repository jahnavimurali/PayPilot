import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import ReCAPTCHA from "react-google-recaptcha";
import "../styles/auth.css";
import "../styles/landing.css";
import { Link } from "react-router-dom";


const siteKey = process.env.REACT_APP_RECAPTCHA_SITE_KEY;
console.log(siteKey)

const Login = () => {
    const navigate = useNavigate();

    const [loginData, setLoginData] = useState({
        email: "",
        password: "",
        captcha: ""
    });

    const handleChange = (e) => {
        setLoginData({ ...loginData, [e.target.name]: e.target.value });
    };

    const handleCaptchaChange = (value) => {
        // value is the token string returned by Google
        setLoginData((prev) => ({ ...prev, captcha: value }));
    };

    const handleLogin = async (e) => {
        e.preventDefault();
        try {
            if (!loginData.captcha) {
                alert("Please verify the captcha!");
                return;
            }

            const res = await axios.post("http://localhost:9090/api/user/login", loginData);
            localStorage.setItem("user", JSON.stringify(res.data));
            toast.success("Login successful!");
            //console.log("Form submitted with captcha token:", loginData.captcha);
            navigate("/dashboard");
        } catch (err) {
            console.error("Login failed:", err);
            toast.error("Login failed: " + (err.response?.data || err.message));
        }
    };

    return (
        <div className="pp-auth-wrap">
            <div className="pp-card pp-auth-card position-relative">
                <div className="pp-auth-logo">Pay<span>Pilot</span></div>
                <h2 className="pp-auth-title">Welcome back</h2>
                <p className="pp-auth-sub">Log in to manage your bills.</p>

                <form onSubmit={handleLogin} className="pp-form-grid">
                    <div className="pp-field">
                        <label htmlFor="email">Email</label>
                        <input
                            id="email"
                            type="email"
                            name="email"
                            className="pp-input"
                            value={loginData.email}
                            onChange={handleChange}
                            placeholder="you@example.com"
                            required
                        />
                    </div>

                    <div className="pp-field">
                        <label htmlFor="password">Password</label>
                        <input
                            id="password"
                            type="password"
                            name="password"
                            className="pp-input"
                            value={loginData.password}
                            onChange={handleChange}
                            placeholder="••••••••"
                            required
                        />
                    </div>

                    <div className="text-end" style={{ marginTop: "6px" }}>
                        <Link to="/send-otp" className="pp-link">Forgot password?</Link>
                    </div>

                    <ReCAPTCHA
                        sitekey={siteKey}
                        onChange={handleCaptchaChange}
                    />
                    <button type="submit" className="pp-btn pp-btn--primary pp-btn--block">
                        Log In
                    </button>
                </form>

                <div className="pp-sep"><span>or</span></div>

                <Link to="/signup" className="pp-btn pp-btn--outline pp-btn--block">
                    Create Account
                </Link>

                <div className="text-center mt-3">
                    <Link to="/" className="pp-btn pp-btn--ghost pp-btn--sm">← Back to Home</Link>
                </div>
            </div>
        </div>

    );
};

export default Login;
