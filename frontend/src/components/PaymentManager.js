import React, { useState, useEffect } from "react";
import axios from "axios";

const PaymentManager = () => {
    const user = JSON.parse(localStorage.getItem("user"));
    const [payments, setPayments] = useState([]);
    const [bills, setBills] = useState([]);
    const [formData, setFormData] = useState({
        billId: "",
        amount: "",
        method: "",
        scheduledDate: "",
    });
    const [editingId, setEditingId] = useState(null);

    useEffect(() => {
        fetchPayments();
        fetchBills();
    }, []);

    const fetchPayments = async () => {
        try {
            const res = await axios.get(`http://localhost:9090/api/payments/user/${user.id}`);
            setPayments(res.data);
        } catch (err) {
            console.error("Error fetching payments:", err);
        }
    };

    const fetchBills = async () => {
        try {
            const res = await axios.get(`http://localhost:9090/api/bills`); // there is a function to call the user's bills as well
            const userBills = res.data.filter((b) => b.userId === user.id);
            setBills(userBills);
        } catch (err) {
            console.error("Error fetching bills:", err);
        }
    };

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleAddOrUpdate = async (e) => {
        e.preventDefault();
        try {
            if (editingId) {
                await axios.put(`http://localhost:9090/api/payments/${editingId}`, {
                    ...formData,
                    userId: user.id,
                });
                alert("✅ Payment updated!");
            } else {
                await axios.post("http://localhost:9090/api/payments", {
                    ...formData,
                    userId: user.id,
                });
                alert("✅ Payment added!");
            }

            fetchPayments();
            setFormData({ billId: "", amount: "", method: "", scheduledDate: "" });
            setEditingId(null);
        } catch (err) {
            console.error("Error:", err);
            alert("❌ Operation failed");
        }
    };

    const handleEdit = (payment) => {
        setFormData({
            billId: payment.billId,
            amount: payment.amount,
            method: payment.method,
            scheduledDate: payment.scheduledDate,
        });
        setEditingId(payment.id);
    };

    const handleDelete = async (id) => {
        if (!window.confirm("Delete this payment?")) return;
        try {
            await axios.delete(`http://localhost:9090/api/payments/${id}`);
            fetchPayments();
        } catch (err) {
            console.error("Delete failed", err);
        }
    };

    return (
        <div className="container mt-4">
            <h3>{editingId ? "Update Payment" : "Add Payment"}</h3>
            <form onSubmit={handleAddOrUpdate}>
                <div className="mb-2">
                    <label>Bill</label>
                    <select
                        className="form-control"
                        name="billId"
                        value={formData.billId}
                        onChange={handleChange}
                        required
                    >
                        <option value="">-- Select a bill --</option>
                        {/* Fallback option during edit (bill not in list) */}
                        {!bills.find(b => b.id === parseInt(formData.billId)) && formData.billId && (
                            <option value={formData.billId}>
                                Unknown Bill #{formData.billId}
                            </option>
                        )}
                        {bills.map((bill) => (
                            <option key={bill.id} value={bill.id}>
                                {bill.title} - ₹{bill.amount} (Due: {bill.dueDate})
                            </option>
                        ))}
                    </select>
                </div>

                <div className="mb-2">
                    <label>Amount</label>
                    <input
                        type="number"
                        name="amount"
                        className="form-control"
                        value={formData.amount}
                        onChange={handleChange}
                        required
                    />
                </div>

                <div className="mb-2">
                    <label>Method</label>
                    <input
                        type="text"
                        name="method"
                        className="form-control"
                        value={formData.method}
                        onChange={handleChange}
                        required
                    />
                </div>

                <div className="mb-2">
                    <label>Scheduled Date</label>
                    <input
                        type="date"
                        name="scheduledDate"
                        className="form-control"
                        value={formData.scheduledDate}
                        onChange={handleChange}
                        required
                    />
                </div>

                <button className="btn btn-primary">{editingId ? "Update" : "Add"}</button>
            </form>

            <hr />

            <h4>All Payments</h4>
            <table className="table table-bordered">
                <thead>
                <tr>
                    <th>Payment ID</th>
                    <th>Bill</th>
                    <th>Amount</th>
                    <th>Method</th>
                    <th>Scheduled Date</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {payments.map((p) => (
                    <tr key={p.id}>
                        <td>{p.id}</td>
                        <td>
                            {bills.find((b) => b.id === p.billId)?.title || `Bill #${p.billId}`}
                        </td>
                        <td>{p.amount}</td>
                        <td>{p.method}</td>
                        <td>{p.scheduledDate}</td>
                        <td>
                            <button className="btn btn-sm btn-warning me-1" onClick={() => handleEdit(p)}>Edit</button>
                            <button className="btn btn-sm btn-danger" onClick={() => handleDelete(p.id)}>Delete</button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

export default PaymentManager;
