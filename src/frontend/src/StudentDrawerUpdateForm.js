import {Drawer, Input, Col, Select, Form, Row, Button, Spin} from 'antd';
import {updateStudent} from "./client";
import {LoadingOutlined} from "@ant-design/icons";
import {useEffect, useState} from "react";
import {errorNotification, successNotification} from "./Notification";

const {Option} = Select;

const antIcon = <LoadingOutlined style={{ fontSize: 24 }} spin />;

function StudentDrawerUpdateForm({showUpdateDrawer, setShowUpdateDrawer, fetchStudents, editingStudent}) {

    const [form] = Form.useForm();  // create a Form control instance
    useEffect(() => {
        form.setFieldsValue(editingStudent)
    }, [form, editingStudent]);

    const onCLose = () => setShowUpdateDrawer(false);
    const [submitting, setSubmitting] = useState(false);
    const onFinish = student => {
        setSubmitting(true);
        console.log(JSON.stringify(student, null, 2));
        updateStudent({...student, id:editingStudent.id})
            .then(() => {
                console.log("student added");
                onCLose();
                successNotification(
                    "Student successfully updated",
                    `${student.name} was updated to the system`
                );
                fetchStudents();
            }).catch(err => {
                console.log(err);
                err.response.json().then(res => {
                    console.log(res);
                    errorNotification(
                        "There was an issue",
                        `${res.message} [${res.status}] [${res.error}]`,
                        "bottomLeft"
                    );
                })
            }).finally(() => {
                setSubmitting(false);
            }
        )
    };

    const onFinishFailed = errorInfo => {
        alert(JSON.stringify(errorInfo, null, 2));
    };


    return <Drawer
        forceRender
        title="Update student info"
        width={720}
        onClose={onCLose}
        visible={showUpdateDrawer}
        bodyStyle={{paddingBottom: 80}}
        footer={
            <div
                style={{
                    textAlign: 'right',
                }}
            >
                <Button onClick={onCLose} style={{marginRight: 8}}>
                    Cancel
                </Button>
            </div>
        }
    >
        <Form layout="vertical"
              form={form}
              onFinishFailed={onFinishFailed}
              onFinish={onFinish}
              hideRequiredMark>
            <Row gutter={16}>
                <Col span={12}>
                    <Form.Item
                        name="name"
                        label="Name"
                        rules={[{required: true, message: 'Please enter student name'}]}
                    >
                        <Input placeholder="Please enter student name"/>
                    </Form.Item>
                </Col>
                <Col span={12}>
                    <Form.Item
                        name="email"
                        label="Email"
                        rules={[{required: true, message: 'Please enter student email'}]}
                    >
                        <Input placeholder="Please enter student email"/>
                    </Form.Item>
                </Col>
            </Row>
            <Row gutter={16}>
                <Col span={12}>
                    <Form.Item
                        name="gender"
                        label="gender"
                        rules={[{required: true, message: 'Please select a gender'}]}
                    >
                        <Select placeholder="Please select a gender">
                            <Option value="MALE">MALE</Option>
                            <Option value="FEMALE">FEMALE</Option>
                            <Option value="OTHER">OTHER</Option>
                        </Select>
                    </Form.Item>
                </Col>
            </Row>
            <Row>
                <Col span={12}>
                    <Form.Item >
                        <Button type="primary" htmlType="submit">
                            Submit
                        </Button>
                    </Form.Item>
                </Col>
            </Row>
            <Row>
                {submitting && <Spin indicator={antIcon} />}
            </Row>
        </Form>
    </Drawer>
}

export default StudentDrawerUpdateForm;