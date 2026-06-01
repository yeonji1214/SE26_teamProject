function IssueAutoInfoCard() {
  return (
    <aside className="issue-create-side-card">
      <h3>자동 설명 정보</h3>
      <ul>
        <li>리포터는 현재 로그인한 사용자로 자동 저장됩니다.</li>
        <li>등록일은 이슈 생성 시점으로 자동 저장됩니다.</li>
        <li>담당자와 수행자는 초기에는 비어 있습니다.</li>
      </ul>
    </aside>
  );
}

export default IssueAutoInfoCard;