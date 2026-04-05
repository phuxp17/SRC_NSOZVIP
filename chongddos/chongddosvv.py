import subprocess
import time
import mysql.connector
from mysql.connector import Error

def create_connection():
    try:
        connection = mysql.connector.connect(
            host='localhost',  # Thay thế bằng địa chỉ host của bạn
            database='nso',  # Thay thế bằng tên cơ sở dữ liệu của bạn
            user='root',  # Thay thế bằng tên người dùng của bạn
            password=''  # Thay thế bằng mật khẩu của bạn
        )
        if connection.is_connected():
            return connection
    except Error as e:
        print(f"Error: {e}")
        return None

def add_ip_to_db(ip, reason):
    connection = create_connection()
    if connection:
        cursor = connection.cursor()
        cursor.execute("INSERT INTO blockip_list (blocker_ip, block_reason, blocked_at) VALUES (%s, %s, NOW())", (ip, reason))
        connection.commit()
        cursor.close()
        connection.close()

def remove_ip_from_db(ip):
    connection = create_connection()
    if connection:
        cursor = connection.cursor()
        cursor.execute("DELETE FROM blockip_list WHERE blocker_ip = %s", (ip,))
        connection.commit()
        cursor.close()
        connection.close()

def get_all_blocked_ips():
    connection = create_connection()
    if connection:
        cursor = connection.cursor()
        cursor.execute("SELECT blocker_ip FROM blockip_list")
        result = cursor.fetchall()
        cursor.close()
        connection.close()
        return [row[0] for row in result]
    return []

def get_existing_rule(rule_name):
    command = f"netsh advfirewall firewall show rule name=\"{rule_name}\""
    try:
        output = subprocess.check_output(command, shell=True).decode("utf-8")
        if "No rules match" not in output:
            return output
    except subprocess.CalledProcessError:
        return None
    return None

def get_ips_from_rule(rule_output):
    lines = rule_output.splitlines()
    for line in lines:
        if line.strip().startswith("RemoteIP:"):
            ips = line.split(":")[1].strip()
            return ips.split(",")
    return []

def add_firewall_rule(rule_name, ip):
    add_firewall_command = f"netsh advfirewall firewall add rule name=\"{rule_name}\" dir=in action=block remoteip={ip}"
    subprocess.call(add_firewall_command, shell=True)

def update_firewall_rule(rule_name, ips):
    delete_firewall_rule(rule_name)
    add_firewall_rule(rule_name, ips)

def delete_firewall_rule(rule_name):
    delete_firewall_command = f"netsh advfirewall firewall delete rule name=\"{rule_name}\""
    subprocess.call(delete_firewall_command, shell=True)

def block_ip(ip, reason=""):
    rule_name = "Block Multiple IPs"
    existing_rule = get_existing_rule(rule_name)
    
    if existing_rule:
        current_ips = get_ips_from_rule(existing_rule)
        if ip not in current_ips:
            current_ips.append(ip)
            updated_ips = ",".join(current_ips)
            update_firewall_rule(rule_name, updated_ips)
            add_ip_to_db(ip, reason)  # Thêm IP vào cơ sở dữ liệu
    else:
        add_firewall_rule(rule_name, ip)
        add_ip_to_db(ip, reason)  # Thêm IP vào cơ sở dữ liệu

def unblock_ip(ip):
    rule_name = "Block Multiple IPs"
    existing_rule = get_existing_rule(rule_name)
    
    if existing_rule:
        current_ips = get_ips_from_rule(existing_rule)
        if ip in current_ips:
            current_ips.remove(ip)
            if current_ips:
                updated_ips = ",".join(current_ips)
                update_firewall_rule(rule_name, updated_ips)
            else:
                delete_firewall_rule(rule_name)
            remove_ip_from_db(ip)  # Xóa IP khỏi cơ sở dữ liệu

def get_active_sockets(port):
    command = f"netstat -nao | findstr :{port}"
    output = subprocess.check_output(command, shell=True).decode("utf-8")
    ip_sockets = {}
    lines = output.splitlines()
    for line in lines:
        parts = line.split()
        if len(parts) >= 5 and parts[1].endswith(f":{port}"):
            ip_address = parts[2].split(":")[0]
            socket_id = parts[4]
            if ip_address not in ip_sockets:
                ip_sockets[ip_address] = []
            ip_sockets[ip_address].append(socket_id)
    return ip_sockets

def write_to_file(file_path, data):
    with open(file_path, "a") as file:
        file.write(data)

def main():
    file_path = "chongddos/ip.txt"
    blocked_file_path = "chongddos/blockedvv_ips.txt"
    port = 14444
    ip_address_limit = 10

    while True:
        ip_sockets = get_active_sockets(port)

        with open(file_path, "w") as file:
            for ip, sockets in ip_sockets.items():
                file.write(f"IP: {ip}\n")
                print(f"Đã ghi địa chỉ IP và số socket vào tệp {file_path}")
                file.write(f"Sockets: {len(sockets)}\n\n")
                if len(sockets) > ip_address_limit:
                    write_to_file(blocked_file_path, f"Blocked IP: {ip}\n")
                    block_ip(ip, "Too many connections")  # Chặn IP và thêm lý do vào cơ sở dữ liệu

        # Kiểm tra và bỏ chặn các IP không còn trong danh sách cơ sở dữ liệu
        blocked_ips_in_db = get_all_blocked_ips()
        rule_name = "Block Multiple IPs"
        existing_rule = get_existing_rule(rule_name)
        if existing_rule:
            current_ips = get_ips_from_rule(existing_rule)
            for ip in current_ips:
                if ip not in blocked_ips_in_db:
                    unblock_ip(ip)

        time.sleep(60)  # Chờ 1 phút

if __name__ == "__main__":
    main()
