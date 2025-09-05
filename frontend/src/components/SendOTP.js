import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import "../styles/auth.css";
import "../styles/landing.css";
import { Link } from "react-router-dom";


const SendOTP = () => {
    const navigate = useNavigate();

    const [email, setEmail] = useState({
        email: "",
    });

    const handleChange = (e) => {
        setEmail({ ...email, [e.target.name]: e.target.value });
    };


    const handleSendOTP = async (e) => {
        e.preventDefault();
        try {
            const res = await axios.post("http://localhost:9090/api/send-otp", email);
            toast.success("OTP sent successfully!");
            navigate("/reset-password");
        } catch (err) {
            console.error("OTP sending failed:", err);
            toast.error("OTP sending failed: " + (err.response?.data || err.message));
        }
    };

    return (
        <div className="pp-auth-wrap">
            <div className="pp-card pp-auth-card position-relative">
                <div className="pp-auth-logo">Pay<span>Pilot</span></div>
                <h2 className="pp-auth-title">Forgot Password</h2>
                <p className="pp-auth-sub">Enter your email to receive a password reset OTP.</p>

                <form onSubmit={handleSendOTP} className="pp-form-grid">
                    <div className="pp-field">
                        <label htmlFor="email">Email</label>
                        <input
                            id="email"
                            type="email"
                            name="email"
                            className="pp-input"
                            value={email.email}
                            onChange={handleChange}
                            placeholder="you@example.com"
                            required
                        />
                    </div>

                    <button type="submit" className="pp-btn pp-btn--primary pp-btn--block">
                        Send OTP
                    </button>
                </form>
                <div className="text-center mt-3">
                    <Link to="/" className="pp-btn pp-btn--ghost pp-btn--sm">← Back to Home</Link>
                </div>
            </div>
        </div>

    );
};

export default SendOTP;
