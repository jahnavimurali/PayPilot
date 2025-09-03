import React, { useState } from "react";
import axios from "axios";
import { useNavigate, Link } from "react-router-dom";
import { toast } from "react-toastify";
import "../styles/auth.css";
import "../styles/landing.css";

const Signup = () => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        name: "",
        email: "",
        password: "",
        panCardNumber:"",
        bankAccountNumber:"",
        ifscCode:"",
        bankingPartner:""
    });
    const [loading, setLoading] = useState(false);
    const [passwordMismatch,setPasswordMismatch]=useState(null);

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleConfirmChange = (e) => {
        if(formData.password===""){
            setPasswordMismatch(null);
        }
        else if(e.target.value!==formData.password){
            setPasswordMismatch(false);
        } else {
            setPasswordMismatch(true);
        }
    }

    const handleSubmit = async (e) => {
        e.preventDefault();
        if(passwordMismatch===false || passwordMismatch===null){
            toast.error("Password Does not match");
            return;
        }
        if (loading) return;
        setLoading(true);
        try {
            await axios.post("http://localhost:9090/api/user/signup", formData);
            toast.success("Signup successful! Redirecting to login…");
            setTimeout(() => navigate("/login"), 1200);
        } catch (err) {
            console.error("Signup failed:", err);
            toast.error("Signup failed: " + (err.response?.data?.message || err.response?.data || err.message));
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="pp-auth-wrap">
            <div className="pp-card pp-auth-card position-relative">
                <div className="pp-auth-logo">Pay<span>Pilot</span></div>
                <h2 className="pp-auth-title">Create your account</h2>
                <p className="pp-auth-sub">It only takes a minute.</p>

                <form onSubmit={handleSubmit} className="pp-form-grid">
                    <div className="pp-field">
                        <label htmlFor="name">Name</label>
                        <input
                            id="name"
                            className="pp-input"
                            type="text"
                            name="name"
                            value={formData.name}
                            onChange={handleChange}
                            placeholder="Jane Doe"
                            required
                        />
                    </div>

                    <div className="pp-field">
                        <label htmlFor="email">Email</label>
                        <input
                            id="email"
                            className="pp-input"
                            type="email"
                            name="email"
                            value={formData.email}
                            onChange={handleChange}
                            placeholder="you@example.com"
                            required
                        />
                    </div>

                    <div className="pp-field">
                        <label htmlFor="password">Password</label>
                        <input
                            id="password"
                            className="pp-input"
                            type="password"
                            name="password"
                            value={formData.password}
                            onChange={handleChange}
                            placeholder="Create a strong password"
                            required
                        />
                    </div>

                    <div className="pp-field">
                        <label htmlFor="password">Confirm Password</label>
                        <div className="passwordWarning">
                            {passwordMismatch === null ? "" : passwordMismatch ? "✅" : "Password does not match!"}
                        </div>
                        <input
                            id="cpassword"
                            className="pp-input"
                            type="password"
                            name="cpassword"
                            onChange={handleConfirmChange}
                            placeholder="Confirm Password"
                            required
                        />
                    </div>

                    <div className="pp-field">
                        <label htmlFor="panCardNumber">Pan Card Number</label>
                        <input
                            id="panCardNumber"
                            className="pp-input"
                            type="text"
                            name="panCardNumber"
                            value={formData.panCardNumber}
                            onChange={handleChange}
                            placeholder="ABC123456"
                            required
                        />
                    </div>

                    <div className="pp-field">
                        <label htmlFor="bankAccountNumber">Bank Account Number</label>
                        <input
                            id="bankAccountNumber"
                            className="pp-input"
                            type="text"
                            name="bankAccountNumber"
                            value={formData.bankAccountNumber}
                            onChange={handleChange}
                            placeholder="XXXXXXXXXXXX"
                            required
                        />
                    </div>

                    <div className="pp-field">
                        <label htmlFor="ifscCode">IFSC Code</label>
                        <input
                            id="ifscCode"
                            className="pp-input"
                            type="text"
                            name="ifscCode"
                            value={formData.ifscCode}
                            onChange={handleChange}
                            placeholder="XXXXXXX"
                            required
                        />
                    </div>

                    <div className="pp-field">
                        <label htmlFor="bankingPartner">Banking Partner</label>
                        <select
                            id="bankingPartner"
                            className="pp-input"
                            name="bankingPartner"
                            value={formData.bankingPartner}
                            onChange={handleChange}
                            required>
                            <option value="">--Select--</option>
                            <option value="hdfc">HDFC</option>
                            <option value="sbi">SBI</option>
                            <option value="pnb">PNB</option>
                        </select>
                    </div>

                    <button type="submit" className="pp-btn pp-btn--primary pp-btn--block" disabled={loading}>
                        {loading ? "Creating…" : "Create Account"}
                    </button>
                </form>

                <div className="pp-sep"><span>or</span></div>

                <Link to="/login" className="pp-btn pp-btn--outline pp-btn--block">
                    Log In
                </Link>

                <div className="text-center mt-3">
                    <Link to="/" className="pp-btn pp-btn--ghost pp-btn--sm">← Back to Home</Link>
                </div>
            </div>
        </div>
    );
};

export default Signup;
